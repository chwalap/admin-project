package com.walkingaverage;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Temperature {
  @JsonProperty("temperature")
  Double temperature;

  Temperature() {
    temperature = 0.0;
  }

  Double getTemperature() {
    return temperature;
  }

  void setTemperature(Double t) {
    temperature = t;
  }
}
