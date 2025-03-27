package com.theaaronrussell.webchat.util;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EventName {

  LOG_IN("log_in"),
  DISCONNECT("disconnect"),
  MESSAGE("message"),
  ERROR("error");

  private final String value;

  EventName(String value) {
    this.value = value;
  }

  @JsonValue
  @Override
  public String toString() {
    return value;
  }
}
