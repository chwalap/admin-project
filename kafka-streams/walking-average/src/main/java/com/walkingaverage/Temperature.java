package com.walkingaverage;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Temperature {
  @JsonProperty("temperature")
  Double temperature;

  Temperature() {
    temperature = 0.0;
  }

  Temperature(Double d) {
    temperature = d;
  }

  Double getTemperature() {
    return temperature;
  }

  void setTemperature(Double t) {
    temperature = t;
  }
}
