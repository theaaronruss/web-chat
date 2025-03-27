package com.theaaronrussell.webchat;

public class ChatEvent {

  private EventName eventName;
  private String user;
  private String content;

  public ChatEvent() {
  }

  public ChatEvent(EventName eventName, String user, String content) {
    this.eventName = eventName;
    this.user = user;
    this.content = content;
  }

  public EventName getEventName() {
    return eventName;
  }

  public void setEventName(EventName eventName) {
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
