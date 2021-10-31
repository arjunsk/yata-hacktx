package com.drykode.yata.processor.sentence.functions;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.RichMapFunction;

import java.time.Instant;

@Slf4j
public class AppendTimeFunction extends RichMapFunction<String, String> {

  @Override
  public String map(String input) throws Exception {

    String result = "The time is " + Instant.now() + " " + input;

    return result;
  }
}
