package com.theaaronrussell.webchat.util;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EventType {

  NAME("name"),
  MESSAGE("message"),
  JOIN("join");

  private final String value;

  EventType(String value) {
    this.value = value;
  }

  @JsonValue
  @Override
  public String toString() {
    return value;
  }

}
