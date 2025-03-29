package com.theaaronrussell.webchat.handler;

import com.theaaronrussell.webchat.service.ChatService;
import com.theaaronrussell.webchat.util.ChatClientManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

  private static final Logger log = LoggerFactory.getLogger(ChatWebSocketHandler.class);
  private final ChatService chatService;
  private final ChatClientManager chatClientManager;

  @Autowired
  public ChatWebSocketHandler(ChatService chatService, ChatClientManager chatClientManager) {
    this.chatService = chatService;
    this.chatClientManager = chatClientManager;
  }

  @Override
  public void afterConnectionEstablished(@NonNull WebSocketSession session) {
    log.info("New client connected from {}", session.getRemoteAddress());
    chatClientManager.addClient(session);
  }

  @Override
  public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
    log.info("Client disconnected from {}", session.getRemoteAddress());
    chatClientManager.removeClient(session.getId());
  }

}
