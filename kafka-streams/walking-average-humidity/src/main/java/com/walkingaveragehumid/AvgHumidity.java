package com.walkingaveragehumid;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AvgHumidity extends Humidity {
  @JsonProperty("walking_average")
  Double walkingAverage;

  AvgHumidity() {
    super();
    walkingAverage = humidity;
  }

  AvgHumidity(Double avg, Double humid) {
    super(humid);
    walkingAverage = avg;
  }

  void setWalkingAverage(Double a) {
    walkingAverage = a;
  }

  Double getWalkingAverage() {
    return walkingAverage;
  }
}
