package com.drykode.yata.processor.sentence.domain;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Emotion {

    @JsonProperty(value = "Bored")
    Float bored;
    @JsonProperty(value = "Angry")
    Float angry;
    @JsonProperty(value = "Sad")
    Float sad;
    @JsonProperty(value = "Fear")
    Float fear;
    @JsonProperty(value = "Excited")
    Float excited;
    @JsonProperty(value = "Happy")
    Float happy;

}