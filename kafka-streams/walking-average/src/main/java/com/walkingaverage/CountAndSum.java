package com.walkingaverage;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CountAndSum {

  private static final Logger logger = LogManager.getLogger(WalkingAverage.class);

  @JsonProperty("count")
  Long count;
  @JsonProperty("sum")
  Double sum;
  @JsonProperty("latest_temp")
  Double latest_temp;

  CountAndSum() {
    count = 0L;
    sum = 0.0;
    latest_temp = 0.0;
  }

  CountAndSum(Long l, Double d, Double latest) {
    count = l;
    sum = d;
    latest_temp = latest;
  }

  void setCount(Long c) {
    count = c;
  }

  void setSum(Double s) {
    sum = s;
  }

  void setLatestTemp(Double d) {
    latest_temp = d;
    logger.always().log("Updated latest Temp: " + d);
  }

  void incCount() {
    count += 1;
  }

  void incSum(Double d) {
    sum += d;
  }

  Double getSum() {
    return sum;
  }

  Long getCount() {
    return count;
  }

  Double getLatestTemp() {
    return latest_temp;
  }
}
