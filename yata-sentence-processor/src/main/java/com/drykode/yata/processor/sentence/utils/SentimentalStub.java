package com.drykode.yata.processor.sentence.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SentimentalStub {

  public String emotion(String s) {
    if (s.contains("angry")) {
      return "Angry";
    } else if (s.contains("Happy")) {
      return "Happy";
    } else if (s.contains("Satisfied")) {
      return "Happy";
    }
    return "Happy";
  }
}
