package com.theaaronrussell.webchat.chat;

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

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

  private static final Logger log = LoggerFactory.getLogger(ChatWebSocketHandler.class);

  private final ConcurrentHashMap<String, WebSocketSession> activeSessions = new ConcurrentHashMap<>();
  private final ObjectMapper objectMapper;

  @Autowired
  public ChatWebSocketHandler(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  /**
   * Keep track of any new connections.
   * {@inheritDoc}
   *
   * @param session The {@code WebSocketSession} associated with the new connection.
   * @throws Exception
   */
  @Override
  public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
    super.afterConnectionEstablished(session);
    activeSessions.put(session.getId(), session);
    log.info("Connection established with a session ID of {}", session.getId());
  }

  /**
   * Stop tracking when client disconnects.
   * {@inheritDoc}
   *
   * @param session The {@code WebSocketSession} associated with the connection.
   * @param status The status code and reason for disconnecting.
   * @throws Exception
   */
  @Override
  public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
    super.afterConnectionClosed(session, status);
    activeSessions.remove(session.getId());
    log.info("Connection with session of ID {} closed", session.getId());
  }

  /**
   * Handles incoming event messages from the WebSocket clients.
   * {@inheritDoc}
   *
   * @param session The {@code WebSocketSession} associated with the incoming event message.
   * @param message The incoming event message.
   * @throws Exception
   */
  @Override
  protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
    super.handleTextMessage(session, message);
    ChatEvent event = parseEvent(message.getPayload());
    if (event == null || event.getEventName() == null) {
      log.error("Received invalid event");
      return;
    }
    final String sessionId = session.getId();
    final String content = event.getContent();
    switch (event.getEventName()) {
      case ChatEvent.EVENT_MESSAGE:
        if (content == null || content.isBlank()) {
          log.warn("Ignoring empty message");
          return;
        }
        log.info("{} says: {}", sessionId, content);
        ChatEvent outgoingEvent = new ChatEvent(ChatEvent.EVENT_MESSAGE, sessionId + " says: " + content);
        broadcastMessage(outgoingEvent);
        break;
      default:
        log.error("Unknown event name \"{}\"", event.getEventName());
        break;
    }
  }

  /**
   * Parse the incoming JSON message into a {@code ChatEvent}.
   *
   * @param message The incoming JSON message from the WebSocket client.
   * @return The parsed {@code ChatEvent}.
   */
  private ChatEvent parseEvent(String message) {
    ChatEvent event = null;
    try {
      event = objectMapper.readValue(message, ChatEvent.class);
    } catch (JsonProcessingException e) {
      log.error("Failed to parse incoming event: {}", message);
    }
    return event;
  }

  /**
   * Broadcast a message to all active WebSocket sessions.
   *
   * @param message Message to broadcast.
   */
  private void broadcastMessage(ChatEvent message) throws JsonProcessingException {
    String messageJson = objectMapper.writeValueAsString(message);
    activeSessions.forEach((id, session) -> {
      try {
        session.sendMessage(new TextMessage(messageJson));
      } catch (IOException e) {
        log.error("Failed to send message to session with ID of {}", id);
      }
    });
  }

}
