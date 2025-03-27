package com.theaaronrussell.webchat.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theaaronrussell.webchat.dto.ChatEvent;
import com.theaaronrussell.webchat.exception.ValidationException;
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

  private static final String EVENT_MESSAGE = "message";
  private static final Logger log = LoggerFactory.getLogger(ChatWebSocketHandler.class);

  private final ChatService chatService;
  private final ObjectMapper objectMapper;

  @Autowired
  public ChatWebSocketHandler(ChatService chatService, ObjectMapper objectMapper) {
    this.chatService = chatService;
    this.objectMapper = objectMapper;
  }

  /**
   * Handle new client connections.
   *
   * @param session The {@code WebSocketSession} related to the client.
   * @throws Exception If any exception occurs.
   */
  @Override
  public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
    super.afterConnectionEstablished(session);
    log.info("New connection from {}", session.getRemoteAddress());
    chatService.acceptNewClient(session);
  }

  /**
   * Handle client disconnecting.
   *
   * @param session The {@code WebSocketSession} related to the client.
   * @param status The status code and reason for client disconnect.
   * @throws Exception If any exception occurs.
   */
  @Override
  public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
    super.afterConnectionClosed(session, status);
    log.info("{} has disconnected", session.getRemoteAddress());
    chatService.disconnectClient(session.getId());
  }

  /**
   * Handle incoming event messages.
   *
   * @param session The {@code WebSocketSession} related to the incoming event message.
   * @param message The event message to process.
   * @throws Exception If any exception occurs.
   */
  @Override
  protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
    super.handleTextMessage(session, message);
    ChatEvent event;
    try {
      event = objectMapper.readValue(message.getPayload(), ChatEvent.class);
    } catch (JsonProcessingException e) {
      log.error("Failed to parse incoming message");
      return;
    }
    try {
      validateEventMessage(event);
    } catch (ValidationException e) {
      log.error("Invalid event: {}", e.getMessage());
      return;
    }
    log.info("Incoming event with name \"{}\"", event.getEventName());
    handleEvent(event);
  }

  /**
   * Verify that the given event is valid.
   *
   * @param event The event to validate.
   * @throws ValidationException If event is not a valid event.
   */
  private void validateEventMessage(ChatEvent event) throws ValidationException {
    String eventName = event.getEventName();
    if (eventName == null || eventName.isBlank()) {
      throw new ValidationException("Event name not provided or is blank");
    }
  }

  private void handleEvent(ChatEvent event) {
    switch (event.getEventName()) {
      case EVENT_MESSAGE:
        // TODO: Send message event to service layer
        break;
      default:
        log.error("Unknown event name");
        break;
    }
  }

}
