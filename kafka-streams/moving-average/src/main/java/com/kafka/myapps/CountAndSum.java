package com.kafka.myapps;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CountAndSum {
  @JsonProperty("count")
  Long count;
  @JsonProperty("sum")
  Double sum;

  CountAndSum() {
    count = 0L;
    sum = 0.0;
  }

  CountAndSum(Long l, Double d) {
    count = l;
    sum = d;
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
