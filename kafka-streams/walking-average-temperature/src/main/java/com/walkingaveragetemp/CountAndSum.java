package com.walkingaveragetemp;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CountAndSum {

  private static final Logger logger = LogManager.getLogger(WalkingAverageTemp.class);

  @JsonProperty("count")
  Long count;
  @JsonProperty("sum")
  Double sum;
  @JsonProperty("latest_value")
  Double latest_value;

  CountAndSum() {
    count = 0L;
    sum = 0.0;
    latest_value = 0.0;
  }

  CountAndSum(Long l, Double d, Double latest) {
    count = l;
    sum = d;
    latest_value = latest;
  }

  void setCount(Long c) {
    count = c;
  }

  void setSum(Double s) {
    sum = s;
  }

  void setLatestValue(Double d) {
    latest_value = d;
    logger.always().log("Updated latest value: " + d);
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

  Double getLatestValue() {
    return latest_value;
  }
}
