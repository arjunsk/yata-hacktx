package com.drykode.yata.parser.audio.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KinesisConstants {

  public static final String KINESIS_REGION = "aws.kinesis.region";
  public static final String KINESIS_FAIL_IF_THROTTLED = "aws.kinesis.fail-if-throttled";
  public static final String KINESIS_MAX_CONNECTIONS = "aws.kinesis.max-connections";
  public static final String KINESIS_REQUEST_TIME_OUT = "aws.kinesis.request-time-out";
  public static final String KINESIS_RECORD_TTL = "aws.kinesis.record-ttl";

  public static final String KINESIS_AGGREGATION_ENABLED = "aws.kinesis.aggregation-enabled";
  public static final String KINESIS_AGGREGATION_MAX_SIZE = "aws.kinesis.aggregation-max-size";
  public static final String KINESIS_AGGREGATION_MAX_COUNT = "aws.kinesis.aggregation-max-count";
  public static final String KINESIS_MAX_BUFFER_TIME = "aws.kinesis.max-buffer-time";

  public static final String KINESIS_MAX_RECORDS_IN_FLIGHT = "aws.kinesis.max-records-in-flight";
  public static final String KINESIS_NAME = "aws.kinesis.name";

}
