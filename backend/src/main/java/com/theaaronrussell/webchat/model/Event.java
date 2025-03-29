package com.theaaronrussell.webchat.model;

import com.theaaronrussell.webchat.util.EventType;

public class Event {

  private EventType type;
  private String username;
  private String content;

  public Event() {
  }

  public Event(EventType type, String username, String content) {
    this.type = type;
    this.username = username;
    this.content = content;
  }

  public EventType getType() {
    return type;
  }

  public void setType(EventType type) {
    this.type = type;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

}
