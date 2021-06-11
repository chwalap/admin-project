package com.kafka.myapps;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AvgTemperature extends Temperature {
  @JsonProperty("walking_average")
  Double walkingAverage;

  AvgTemperature() {
    super();
    walkingAverage = temperature;
  }

  AvgTemperature(Double d) {
    super();
    walkingAverage = d;
  }

  void setWalkingAverage(Double a) {
    walkingAverage = a;
  }

  Double getWalkingAverage() {
    return walkingAverage;
  }
}
