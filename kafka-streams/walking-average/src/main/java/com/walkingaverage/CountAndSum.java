package com.walkingaverage;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CountAndSum {
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
}
