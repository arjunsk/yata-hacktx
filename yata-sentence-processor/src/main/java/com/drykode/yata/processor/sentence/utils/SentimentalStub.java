package com.drykode.yata.processor.sentence.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SentimentalStub {

  public String emotion(String s) {
    String str = s.toLowerCase();
    if (str.contains("angry")) {
      return "Angry";
    } else if (str.contains("happy")) {
      return "Happy";
    } else if (str.contains("satisfied")) {
      return "Happy";
    }
    return "Happy";
  }
}
