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
      String line = br.readLine();
      String[] content = line.split("-");

      Map<String, ByteArrayInputStream> contentMap = new HashMap<>();

      System.out.println(content[0]);
      System.out.println(content[1]);

      contentMap.put(content[0], new ByteArrayInputStream(line.getBytes(StandardCharsets.UTF_8)));

      dispatcher.dispatch(contentMap);
    }
  }
}
