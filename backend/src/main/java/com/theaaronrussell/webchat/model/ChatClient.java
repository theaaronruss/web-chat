package com.theaaronrussell.webchat.model;

import org.springframework.web.socket.WebSocketSession;

public class ChatClient {

  private String username;
  private WebSocketSession session;

  public ChatClient(String username, WebSocketSession session) {
    this.username = username;
    this.session = session;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public WebSocketSession getSession() {
    return session;
  }

  public void setSession(WebSocketSession session) {
    this.session = session;
  }

}
