package com.theaaronrussell.webchat.dto;

import org.springframework.web.socket.WebSocketSession;

public class ChatClient {

  private WebSocketSession session;
  private String username;

  public ChatClient() {
  }

  public ChatClient(WebSocketSession session) {
    this.session = session;
  }

  public WebSocketSession getSession() {
    return session;
  }

  public void setSession(WebSocketSession session) {
    this.session = session;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

}
