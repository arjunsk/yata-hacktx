package com.drykode.yata.core.domains;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
public class Sentence {

  private String customerId;

  private String content;
}
