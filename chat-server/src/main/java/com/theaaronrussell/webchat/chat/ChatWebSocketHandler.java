package com.theaaronrussell.webchat.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

  private static final Logger log = LoggerFactory.getLogger(ChatWebSocketHandler.class);

  private final ConcurrentHashMap<String, WebSocketSession> activeSessions = new ConcurrentHashMap<>();

  @Override
  public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
    super.afterConnectionEstablished(session);
    activeSessions.put(session.getId(), session);
    log.info("Connection established with a session ID of {}", session.getId());
  }

  @Override
  public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
    super.afterConnectionClosed(session, status);
    activeSessions.remove(session.getId());
    log.info("Connection with session of ID {} closed", session.getId());
  }

  @Override
  protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
    super.handleTextMessage(session, message);
    activeSessions.forEach((id, activeSession) -> {
      String messageOut = String.format("%s: %s", session.getId(), message.getPayload());
        try {
          activeSession.sendMessage(new TextMessage(messageOut));
        } catch (IOException e) {
          log.error("Failed to send message to session with ID of {}", activeSession.getId());
        }
    });
  }

}
