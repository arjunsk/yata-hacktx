package com.drykode.yata.parser.audio.kinesis.producer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * KPL Config POJO.
 *
 * <p>Default values:
 * https://github.com/awslabs/amazon-kinesis-producer/blob/master/java/amazon-kinesis-producer-sample/default_config.properties
 */
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KplConfig {

  private String region;
  private boolean failIfThrottled;

  private long maxConnections;
  private long requestTimeout;
  private long recordTtl;

  /* Aggregation Params. */
  private boolean aggregationEnabled;
  private long aggregationMaxSize;
  private long aggregationMaxCount;
  private long recordMaxBufferedTime;

  private long maxRecordsInFlight;
}
