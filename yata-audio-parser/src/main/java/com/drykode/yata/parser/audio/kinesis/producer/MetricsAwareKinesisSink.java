package com.drykode.yata.parser.audio.kinesis.producer;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.kinesis.producer.*;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/** Kinesis writer with backpressure handling. */
@Slf4j
public class MetricsAwareKinesisSink {

  private final KinesisProducer kinesisProducer;
  private final ExecutorService callbackThreadPool;

  /** Maximum records in memory that is yet to be send. */
  private final long maxRecordsInFlight;

  /** Every KPL record is assigned a sequenceNumber <= totalRecords. */
  private AtomicLong sequenceNumber;

  /* Count of success and failed records.
   * totalRecords = success + failure */
  private AtomicLong success;
  private AtomicLong failure;

  /**
   * Initializing kinesis client.
   *
   * @param config kpl config for kinesis writer.
   */
  public MetricsAwareKinesisSink(KplConfig config) {

    maxRecordsInFlight = config.getMaxRecordsInFlight();

    KinesisProducerConfiguration configuration =
        new KinesisProducerConfiguration()
            .setLogLevel("warning")
            .setRegion(config.getRegion())
            .setFailIfThrottled(config.isFailIfThrottled())
            .setRecordMaxBufferedTime(config.getRecordMaxBufferedTime())
            .setMaxConnections(config.getMaxConnections())
            .setRequestTimeout(config.getRequestTimeout())
            .setRecordTtl(config.getRecordTtl())
            .setAggregationEnabled(config.isAggregationEnabled())
            .setAggregationMaxCount(config.getAggregationMaxCount())
            .setAggregationMaxSize(config.getAggregationMaxSize())
            .setCredentialsProvider(new DefaultAWSCredentialsProviderChain());

    kinesisProducer = new KinesisProducer(configuration);

    callbackThreadPool = Executors.newCachedThreadPool();
  }

  /**
   * Write list of records to kinesis
   *
   * @param records records to be written to kinesis.
   * @param streamName kinesis stream name.
   * @return boolean value of whether all the records are written or not.
   */
  @SneakyThrows
  public boolean write(Map<String, ByteArrayInputStream> records, String streamName) {

    // initialize counter
    sequenceNumber = new AtomicLong(0);
    success = new AtomicLong(0);
    failure = new AtomicLong(0);

    long totalRecordsToPut = records.size();

    // Success and Failure handler.
    final FutureCallback<UserRecordResult> callback = getFutureCallback();

    // Progress update thread.
    Thread progress = getProgressThread(totalRecordsToPut);
    progress.start();

    // Put records
    records.forEach(
        (partitionKey, record) -> {
          while (sequenceNumber.get() < totalRecordsToPut) {

            // wait if outStanding records is more than the maxRecordsInFlight threshold.
            if (kinesisProducer.getOutstandingRecordsCount() < maxRecordsInFlight) {

              ByteBuffer data = getByteBuffer(record);

              ListenableFuture<UserRecordResult> f =
                  kinesisProducer.addUserRecord(streamName, partitionKey, data);
              Futures.addCallback(f, callback, callbackThreadPool);

              sequenceNumber.getAndIncrement();

              // if we write current record, then break out the while loop.
              break;

            } else {
              delay(1);
            }
          }
        });

    // Wait for remaining records to finish
    while (kinesisProducer.getOutstandingRecordsCount() > 0) {
      kinesisProducer.flush();
      delay(100);
    }

    // wait until the files are completely written
    progress.join();

    // return the boolean stating if the records are completely written.
    return (failure.get() == 0) && (success.get() == totalRecordsToPut);
  }

  public void close() {
    // destroy kinesis producer.
    kinesisProducer.destroy();

    // shutdown thread pool
    try {
      log.debug("Attempt to shutdown executor");
      callbackThreadPool.shutdown();
      callbackThreadPool.awaitTermination(5, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      log.error("tasks interrupted");
    } finally {
      if (!callbackThreadPool.isTerminated()) {
        log.error("Cancel non-finished tasks");
      }
      callbackThreadPool.shutdownNow();
      log.debug("Shutdown finished");
    }
  }

  /**
   * Shows progress of kinesis write.
   *
   * @param totalRecordsToPut total records to be written.
   * @return progress thread.
   */
  private Thread getProgressThread(long totalRecordsToPut) {
    return new Thread(
        () -> {
          while (true) {
            long put = sequenceNumber.get();
            double putPercent = 100.0 * put / totalRecordsToPut;
            long done = success.get() + failure.get();
            double donePercent = 100.0 * done / totalRecordsToPut;
            log.debug(
                String.format(
                    "Put %d of %d so far (%.2f %%), %d have completed (%.2f %%)",
                    put, totalRecordsToPut, putPercent, done, donePercent));

            if (done == totalRecordsToPut) {
              break;
            }

            try {
              for (Metric m : kinesisProducer.getMetrics("UserRecordsPut", 5)) {

                if (m.getDimensions().size() == 1 && m.getSampleCount() > 0) {
                  log.debug(
                      String.format(
                          "(Sliding 5 seconds) Avg put rate: %.2f per sec, success rate: %.2f, failure rate: %.2f, total attempted: %d",
                          m.getSum() / 5,
                          m.getSum() / m.getSampleCount() * 100,
                          (m.getSampleCount() - m.getSum()) / m.getSampleCount() * 100,
                          (long) m.getSampleCount()));
                }
              }
            } catch (Exception e) {
              log.error("Unexpected error getting metrics", e);
              System.exit(1);
            }

            delay(1000);
          }
        });
  }

  /**
   * Get byte buffer from ByteArray Input Stream
   *
   * @param record byte array input stream
   * @return byteBuffer
   */
  @SneakyThrows
  private ByteBuffer getByteBuffer(ByteArrayInputStream record) {
    return ByteBuffer.wrap(IOUtils.toByteArray(record));
  }

  /**
   * Delay by milli seconds.
   *
   * @param millis milliseconds.
   */
  @SneakyThrows
  private void delay(long millis) {
    Thread.sleep(millis);
  }

  /**
   * Future callback.
   *
   * @return FutureCallback for kinesis write status.
   */
  private FutureCallback<UserRecordResult> getFutureCallback() {
    return new FutureCallback<UserRecordResult>() {

      @Override
      public void onFailure(Throwable t) {
        if (t instanceof UserRecordFailedException) {
          Attempt last =
              Iterables.getLast(((UserRecordFailedException) t).getResult().getAttempts());
          log.error(
              String.format(
                  "Record failed to put - %s : %s", last.getErrorCode(), last.getErrorMessage()));
        }
        log.error("Exception during put", t);

        // if failed, increment failure.
        failure.getAndIncrement();
      }

      @Override
      public void onSuccess(UserRecordResult result) {
        // if success, increment success.
        success.getAndIncrement();
      }
    };
  }
}
