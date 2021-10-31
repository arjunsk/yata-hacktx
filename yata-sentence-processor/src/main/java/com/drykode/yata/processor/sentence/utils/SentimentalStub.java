package com.drykode.yata.processor.sentence.utils;

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
      Response response = client.newCall(request).execute();
      return response.body().string();
    } catch (Exception ex) {
      log.debug(ex.toString());
    }

    return "Unknown";
  }
}
