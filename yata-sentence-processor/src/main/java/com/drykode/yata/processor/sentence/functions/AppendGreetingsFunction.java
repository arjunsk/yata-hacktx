package com.drykode.yata.processor.sentence.functions;

import com.drykode.yata.processor.sentence.constants.LogConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.RichMapFunction;

@Slf4j
public class AppendGreetingsFunction extends RichMapFunction<String, String> {

  private final String greetKeyword;

  public AppendGreetingsFunction(String greetKeyword) {
    this.greetKeyword = greetKeyword;
  }

  @Override
  public String map(String input) throws Exception {

    log.debug("AppendGreetingsFunction" + LogConstants.METHOD_START);

    String result = greetKeyword + "! " + input;

    log.debug("AppendGreetingsFunction" + LogConstants.METHOD_END);

    return result;
  }
}
