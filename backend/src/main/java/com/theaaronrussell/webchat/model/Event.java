package com.theaaronrussell.webchat.model;

import com.theaaronrussell.webchat.util.EventType;

public class Event {

  private EventType type;
  private String content;

  public Event() {
  }

  public Event(EventType type, String content) {
    this.type = type;
    this.content = content;
  }

  public EventType getType() {
    return type;
  }

  public void setType(EventType type) {
    this.type = type;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

}
