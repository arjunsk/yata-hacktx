package com.drykode.yata.parser.audio.dispatcher;

import com.drykode.yata.core.support.PropertyFetcher;
import com.drykode.yata.parser.audio.kinesis.producer.KplConfig;
import com.drykode.yata.parser.audio.kinesis.producer.MetricsAwareKinesisSink;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.drykode.yata.parser.audio.constants.KinesisConstants.*;
import static java.lang.Boolean.parseBoolean;
import static java.lang.Long.parseLong;

public class Dispatcher {

  private final MetricsAwareKinesisSink metricsAwareKinesisSink;
  private final PropertyFetcher pf;

  public Dispatcher() {
    pf = new PropertyFetcher("application.properties");
    metricsAwareKinesisSink = new MetricsAwareKinesisSink(loadKplConfig());
  }

  public boolean dispatch(Map<String,ByteArrayInputStream> content) {
    String kinesisName = pf.getPropertyValue(KINESIS_NAME);
    return metricsAwareKinesisSink.write(content, kinesisName);
  }

  @SneakyThrows
  private KplConfig loadKplConfig() {

    String region = pf.getPropertyValue(KINESIS_REGION);
    boolean failIfThrottled = parseBoolean(pf.getPropertyValue(KINESIS_FAIL_IF_THROTTLED));
    long maxConnections = parseLong(pf.getPropertyValue(KINESIS_MAX_CONNECTIONS));
    long requestTimeout = parseLong(pf.getPropertyValue(KINESIS_REQUEST_TIME_OUT));
    long recordTtl = parseLong(pf.getPropertyValue(KINESIS_RECORD_TTL));

    boolean aggregationEnabled = parseBoolean(pf.getPropertyValue(KINESIS_AGGREGATION_ENABLED));
    long aggregationMaxSize = parseLong(pf.getPropertyValue(KINESIS_AGGREGATION_MAX_SIZE));
    long aggregationMaxCount = parseLong(pf.getPropertyValue(KINESIS_AGGREGATION_MAX_COUNT));
    long recordMaxBufferedTime = parseLong(pf.getPropertyValue(KINESIS_MAX_BUFFER_TIME));

    long maxRecordsInFlight = parseLong(pf.getPropertyValue(KINESIS_MAX_RECORDS_IN_FLIGHT));

    return KplConfig.builder()
        .region(region)
        .failIfThrottled(failIfThrottled)
        .maxConnections(maxConnections)
        .requestTimeout(requestTimeout)
        .recordTtl(recordTtl)
        .aggregationEnabled(aggregationEnabled)
        .aggregationMaxSize(aggregationMaxSize)
        .aggregationMaxCount(aggregationMaxCount)
        .recordMaxBufferedTime(recordMaxBufferedTime)
        .maxRecordsInFlight(maxRecordsInFlight)
        .build();
  }
}
