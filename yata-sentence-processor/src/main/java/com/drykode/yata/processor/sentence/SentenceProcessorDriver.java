package com.drykode.yata.processor.sentence;

import com.amazonaws.services.kinesisanalytics.runtime.KinesisAnalyticsRuntime;
import com.drykode.yata.processor.sentence.functions.AppendGreetingsFunction;
import com.drykode.yata.processor.sentence.functions.AppendTimeFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.LocalStreamEnvironment;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kinesis.FlinkKinesisConsumer;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import static com.drykode.yata.processor.sentence.configs.StreamProcessorConfigConstants.*;

@Slf4j
public class SentenceProcessorDriver {
  private static DataStream<String> createSource(
      StreamExecutionEnvironment env, Properties properties) {
    return env.addSource(
            new FlinkKinesisConsumer<>(
                properties.getProperty(KINESIS_SOURCE_STREAM_NAME_KEY),
                new SimpleStringSchema(),
                properties))
        .name("Kinesis Source")
        .uid("kinesis_source");
  }

  public static void main(String[] args) throws Exception {
    final StreamExecutionEnvironment sEnv = StreamExecutionEnvironment.getExecutionEnvironment();

    // Fetching applicationProperties.
    Map<String, Properties> applicationPropertiesMap = initPropertiesMap(sEnv);
    Properties applicationProperties = applicationPropertiesMap.get(STREAM_PROCESSOR_GROUP_ID_KEY);

    // ===== Adding Flink Source. =====
    DataStream<String> inputStream =
        createSource(sEnv, applicationPropertiesMap.get(KINESIS_SOURCE_GROUP_ID_KEY));

    DataStream<String> timeAppendedStream =
        inputStream
            .map(new AppendTimeFunction())
            .name("Time Appender")
            .uid("time_appender")
            .startNewChain();

    String greetingKeyword = applicationProperties.getProperty(STREAM_PROCESSOR_GREETING_KEY);
    DataStream<String> greetAppendedStream =
        timeAppendedStream
            .map(new AppendGreetingsFunction(greetingKeyword))
            .name("Greet Appender")
            .uid("greet_appender")
            .startNewChain();

    greetAppendedStream.print();

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
