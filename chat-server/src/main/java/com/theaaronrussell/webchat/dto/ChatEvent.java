package com.theaaronrussell.webchat.dto;

public class ChatEvent {

  private String eventName;
  private String user;
  private String content;

  public ChatEvent() {
  }

  public ChatEvent(String eventName, String user, String content) {
    this.eventName = eventName;
    this.user = user;
    this.content = content;
  }

  public String getEventName() {
    return eventName;
  }

  public void setEventName(String eventName) {
    this.eventName = eventName;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

}
