package com.theaaronrussell.webchat.model;

public class Event {

  private String content;

  public Event() {}

  public Event(String content) {
    this.content = content;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

}
