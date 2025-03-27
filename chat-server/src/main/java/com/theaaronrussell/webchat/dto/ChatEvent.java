package com.theaaronrussell.webchat.dto;

public class ChatEvent {

  private String eventName;
  private String content;

  public ChatEvent() {
  }

  public ChatEvent(String eventName, String content) {
    this.eventName = eventName;
    this.content = content;
  }

  public String getEventName() {
    return eventName;
  }

  public void setEventName(String eventName) {
    this.eventName = eventName;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

}
