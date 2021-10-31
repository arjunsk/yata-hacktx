package com.drykode.yata.processor.sentence.utils;

import com.drykode.yata.processor.sentence.domain.Emotion;
import com.drykode.yata.processor.sentence.domain.KomprehendDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

@UtilityClass
@Slf4j
public class SentimentalStub {

  public String emotion(String s) {
    // TODO: remove
    String api_key = "";

    String text = "[\"" + s.toLowerCase() + "\"]";
    String url = "https://apis.paralleldots.com/v5/emotion_batch";
    OkHttpClient client = new OkHttpClient();
    ObjectMapper objectMapper = new ObjectMapper();

    RequestBody requestBody =
        new MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("api_key", api_key)
            .addFormDataPart("text", text)
            .build();

    Request request =
        new Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("cache-control", "no-cache")
            .build();

    try {
      ResponseBody responseBody = client.newCall(request).execute().body();
      KomprehendDTO komprehendDTO =
          objectMapper.readValue(responseBody.string(), KomprehendDTO.class);

      return matchEmotion(komprehendDTO);
    } catch (Exception ex) {
      log.debug(ex.toString());
    }

    return "Unknown";
  }

  private String matchEmotion(KomprehendDTO komprehendDTO) {
    StringBuilder emotionOfUser = new StringBuilder("");
    Emotion threshold =
        Emotion.builder()
            .happy(0.1f)
            .angry(0.1f)
            .sad(0.1f)
            .bored(0.1f)
            .excited(0.1f)
            .fear(0.1f)
            .build();
    for (Emotion emotion : komprehendDTO.getEmotion()) {
      if (emotion.getAngry() > threshold.getAngry()) {
        emotionOfUser.append("Angry " + emotion.getAngry() * 100 + "%");
      }

      if (emotion.getHappy() > threshold.getHappy()) {
        emotionOfUser.append(" Happy " + emotion.getHappy() * 100 + "%");
      }

      if (emotion.getSad() > threshold.getSad()) {
        emotionOfUser.append(" Sad " + emotion.getSad() * 100 + "%");
      }

      if (emotion.getFear() > threshold.getFear()) {
        emotionOfUser.append(" Fear " + emotion.getFear() * 100 + "%");
      }

      if (emotion.getBored() > threshold.getBored()) {
        emotionOfUser.append(" Bored " + emotion.getBored() * 100 + "%");
      }

      if (emotion.getExcited() > threshold.getExcited()) {
        emotionOfUser.append(" Excited " + emotion.getExcited() * 100 + "%");
      }
    }

    return emotionOfUser.toString();
  }
}
