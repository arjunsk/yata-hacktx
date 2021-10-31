package com.drykode.yata.parser.audio;

import com.drykode.yata.parser.audio.dispatcher.Dispatcher;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class AudioParserDriver {

  public static void main(String[] args) throws IOException {
    Dispatcher dispatcher = new Dispatcher();

    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    while (true) {
      String[] line = br.readLine().split("-");

      Map<String, ByteArrayInputStream> content = new HashMap<>();

      System.out.println(line[0]);
      System.out.println(line[1]);

      content.put(line[0], new ByteArrayInputStream(line[1].getBytes(StandardCharsets.UTF_8)));

      dispatcher.dispatch(content);
    }
  }
}
