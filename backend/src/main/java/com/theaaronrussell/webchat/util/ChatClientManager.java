package com.theaaronrussell.webchat.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theaaronrussell.webchat.model.ChatClient;
import com.theaaronrussell.webchat.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class for keeping track of the connected chat clients. Allows sending events to individual clients as well
 * as broadcasting to all.
 */
@Component
public class ChatClientManager {

  private static final Logger log = LoggerFactory.getLogger(ChatClientManager.class);
  private final ConcurrentHashMap<String, ChatClient> clients = new ConcurrentHashMap<>();
  private final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * Add a chat client to manage. The given username for the client is {@code null}. If the provided
   * {@code WebSocketSession} is null, then nothing happens.
   *
   * @param webSocketSession The {@code WebSocketSession} associated with the chat client.
   */
  public void addClient(WebSocketSession webSocketSession) {
    if (webSocketSession == null) {
      return;
    }
    ChatClient client = new ChatClient(null, webSocketSession);
    String sessionId = webSocketSession.getId();
    clients.put(sessionId, client);
    log.debug("Client with session ID {} added to client manager", sessionId);
  }

  /**
   * Remove a client from the list of managed chat clients. This does not close the {@code
   * WebSocketSession} associated with the client.
   *
   * @param sessionId ID of the {@code WebSocketSession} associated with the chat client.
   */
  public void removeClient(String sessionId) {
    if (clients.remove(sessionId) != null) {
      log.debug("Client with session ID {} removed from client manager", sessionId);
    } else {
      log.warn("Client with session ID {} not removed from client manager because it was not found", sessionId);
    }
  }

  /**
   * Broadcast event to all connected clients.
   *
   * @param event The event to broadcast.
   */
  public void broadcastEvent(Event event) {
    clients.values().forEach(client -> {
      WebSocketSession session = client.getSession();
      if (!session.isOpen()) {
        log.warn("Client session with ID of {} is not open, not broadcasting message to that client", session.getId());
        return;
      }
      try {
        String eventJson = objectMapper.writeValueAsString(event);
        session.sendMessage(new TextMessage(eventJson));
      } catch (JsonProcessingException e) {
        log.error("Failed to serialize event to JSON");
      } catch (IOException e) {
        log.error("Failed to broadcast message to client with session ID {}", session.getId());
      }
    });
  }

}
