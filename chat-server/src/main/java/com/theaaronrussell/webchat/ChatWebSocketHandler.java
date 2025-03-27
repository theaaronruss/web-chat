package com.theaaronrussell.webchat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
  private final ObjectMapper objectMapper;

  @Autowired
  public ChatWebSocketHandler(ChatService chatService, ObjectMapper objectMapper) {
    this.chatService = chatService;
    this.objectMapper = objectMapper;
  }

  /**
   * Handle new client connection.
   *
   * @param session The {@code WebSocketSession} associated with the client.
   * @throws Exception If any exception occurs.
   */
  @Override
  public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
    super.afterConnectionEstablished(session);
    log.info("New connection from {}", session.getRemoteAddress());
    chatService.acceptNewClient(session);
  }

  /**
   * Handle client disconnection.
   *
   * @param session The {@code WebSocketSession} associated with the client.
   * @param status  The status code and reason for client disconnection.
   * @throws Exception If any exception occurs.
   */
  @Override
  public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
    super.afterConnectionClosed(session, status);
    log.info("{} has disconnected", session.getRemoteAddress());
    chatService.disconnectClient(session.getId());
  }

  /**
   * Handle incoming messages.
   *
   * @param session The {@code WebSocketSession} related to the incoming message.
   * @param message The message to process.
   * @throws Exception If any exception occurs.
   */
  @Override
  protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
    super.handleTextMessage(session, message);
    try {
      ChatEvent event = objectMapper.readValue(message.getPayload(), ChatEvent.class);
      if (event.getEventName() == null) {
        throw new ValidationException("Event name not provided or is blank");
      }
      switch (event.getEventName()) {
        case EventName.LOG_IN -> chatService.logIn(session.getId(), event);
        case EventName.MESSAGE -> chatService.sendMessage(session.getId(), event);
        default -> log.error("Unknown event name");
      }
    } catch (JsonProcessingException e) {
      log.error("Failed to parse incoming message");
    } catch (ValidationException e) {
      log.error("Invalid event: {}", e.getMessage());
    }
  }

}
