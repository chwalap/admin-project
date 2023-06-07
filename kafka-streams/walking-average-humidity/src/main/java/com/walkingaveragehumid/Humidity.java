package com.walkingaveragehumid;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Humidity {
  @JsonProperty("humidity")
  Double humidity;

  Humidity() {
    humidity = 0.0;
  }

  Humidity(Double d) {
    humidity = d;
  }

  Double getHumidity() {
    return humidity;
  }

  void setHumidity(Double t) {
    humidity = t;
  }
}
