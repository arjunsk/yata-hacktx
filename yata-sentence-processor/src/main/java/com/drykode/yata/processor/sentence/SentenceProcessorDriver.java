package com.drykode.yata.processor.sentence;

import com.amazonaws.services.kinesisanalytics.runtime.KinesisAnalyticsRuntime;
import com.drykode.yata.core.domains.Sentence;
import com.drykode.yata.processor.sentence.utils.SentimentalStub;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.LocalStreamEnvironment;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisConsumer;
import org.apache.flink.util.Collector;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import static com.drykode.yata.processor.sentence.configs.StreamProcessorConfigConstants.KINESIS_SOURCE_GROUP_ID_KEY;
import static com.drykode.yata.processor.sentence.configs.StreamProcessorConfigConstants.KINESIS_SOURCE_STREAM_NAME_KEY;

@Slf4j
public class SentenceProcessorDriver {

  public static void main(String[] args) throws Exception {

    final StreamExecutionEnvironment sEnv = StreamExecutionEnvironment.getExecutionEnvironment();
    Map<String, Properties> applicationPropertiesMap = initPropertiesMap(sEnv);

    DataStream<String> textStream =
        sEnv.addSource(
                new FlinkKinesisConsumer<>(
                    applicationPropertiesMap
                        .get(KINESIS_SOURCE_GROUP_ID_KEY)
                        .getProperty(KINESIS_SOURCE_STREAM_NAME_KEY),
                    new SimpleStringSchema(),
                    applicationPropertiesMap.get(KINESIS_SOURCE_GROUP_ID_KEY)))
            .name("Kinesis Source")
            .uid("kinesis_source")
            .startNewChain();

    DataStream<Sentence> pojoStream =
        textStream
            .map(
                (MapFunction<String, Sentence>)
                    s -> {
                      String[] content = s.split("-");
                      return new Sentence(content[0], content[1]);
                    })
            .name("Pojo Converter")
            .uid("pojo_converter")
            .startNewChain();

    DataStream<String> outputStream =
        pojoStream
            .keyBy(Sentence::getCustomerId)
            .window(SlidingProcessingTimeWindows.of(Time.seconds(10), Time.seconds(5)))
            // .trigger(CountTrigger.of(3))
            .process(
                new ProcessWindowFunction<Sentence, String, String, TimeWindow>() {
                  @Override
                  public void process(
                      String key,
                      ProcessWindowFunction<Sentence, String, String, TimeWindow>.Context context,
                      Iterable<Sentence> windowElements,
                      Collector<String> out) {

                    String callerID = "";
                    StringBuilder inputSentence = new StringBuilder();
                    for (Sentence currSentence : windowElements) {
                      inputSentence.append(currSentence.getContent());
                      callerID = currSentence.getCustomerId();
                    }

                    String outputEmotion = SentimentalStub.emotion(inputSentence.toString());
                    out.collect(callerID + " is " + outputEmotion);
                  }
                })
            .name("Last 10 Sec Emotion")
            .uid("last_10sec_emotion")
            .startNewChain();

    outputStream.print();

    sEnv.execute("Flink Streaming Processor");
  }

  private static Map<String, Properties> initPropertiesMap(StreamExecutionEnvironment sEnv)
      throws IOException {

    Map<String, Properties> applicationPropertiesMap;
    if (sEnv instanceof LocalStreamEnvironment) {

      // TODO: Fix this to read from resource folder
      String filePath =
          "/Users/xvamp/Documents/workspace/sem1/#Hackathones/hacktx/yata/yata-sentence-processor/src/main/resources/application-properties-dev.json";
      applicationPropertiesMap = KinesisAnalyticsRuntime.getApplicationProperties(filePath);

      log.info("Read Properties from resource folder.");
    } else {
      applicationPropertiesMap = KinesisAnalyticsRuntime.getApplicationProperties();
      log.info("Read Properties from KDA.");
    }
    return applicationPropertiesMap;
  }
}
