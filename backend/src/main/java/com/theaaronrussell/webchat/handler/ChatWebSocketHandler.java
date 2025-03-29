package com.theaaronrussell.webchat.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theaaronrussell.webchat.model.Event;
import com.theaaronrussell.webchat.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

  private static final Logger log = LoggerFactory.getLogger(ChatWebSocketHandler.class);
  private final ChatService chatService;
  private final ObjectMapper objectMapper =
      new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  @Autowired
  public ChatWebSocketHandler(ChatService chatService) {
    this.chatService = chatService;
  }

  @Override
  public void afterConnectionEstablished(@NonNull WebSocketSession session) {
    log.info("New client connected from {}", session.getRemoteAddress());
    chatService.connectClient(session);
  }

  @Override
  public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
    log.info("Client disconnected from {}", session.getRemoteAddress());
    chatService.disconnectClient(session);
  }

  @Override
  protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) {
    try {
      Event event = objectMapper.readValue(message.getPayload(), Event.class);
      log.info("New event from client with session ID {}", session.getId());
      chatService.processEvent(session.getId(), event);
    } catch (JsonProcessingException e) {
      log.error("Failed to parse incoming WebSocket message");
    }
  }

}
