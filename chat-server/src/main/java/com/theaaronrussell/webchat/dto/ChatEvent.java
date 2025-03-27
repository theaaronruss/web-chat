package com.theaaronrussell.webchat.dto;

public class ChatEvent {

  private String eventName;
  private String author;
  private String content;

  public ChatEvent() {
  }

  public ChatEvent(String eventName, String author, String content) {
    this.eventName = eventName;
    this.author = author;
    this.content = content;
  }

  public String getEventName() {
    return eventName;
  }

  public void setEventName(String eventName) {
    this.eventName = eventName;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

}
