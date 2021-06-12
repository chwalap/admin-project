package com.walkingaverage;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AvgTemperature extends Temperature {
  @JsonProperty("walking_average")
  Double walkingAverage;

  AvgTemperature() {
    super();
    walkingAverage = temperature;
  }

  AvgTemperature(Double avg, Double temp) {
    super(temp);
    walkingAverage = avg;
  }

  void setWalkingAverage(Double a) {
    walkingAverage = a;
  }

  Double getWalkingAverage() {
    return walkingAverage;
  }
}
