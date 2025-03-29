package com.theaaronrussell.webchat.util;

import com.fasterxml.jackson.annotation.JsonInclude;
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
 * Utility class for keeping track of the connected chat clients. Allows sending events to individual clients as well as
 * broadcasting to all.
 */
@Component
public class ChatClientManager {

  private static final Logger log = LoggerFactory.getLogger(ChatClientManager.class);
  private final ConcurrentHashMap<String, ChatClient> clients = new ConcurrentHashMap<>();
  private final ObjectMapper objectMapper = new ObjectMapper()
      .setSerializationInclusion(JsonInclude.Include.NON_NULL);

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
    log.debug("There are now {} connected clients", getNumConnectedClients());
  }

  /**
   * Remove a client from the list of managed chat clients. This does not close the {@code WebSocketSession} associated
   * with the client. This method does nothing if the client is not found.
   *
   * @param sessionId ID of the {@code WebSocketSession} associated with the chat client.
   */
  public void removeClient(String sessionId) {
    if (clients.remove(sessionId) != null) {
      log.debug("Client with session ID {} removed from client manager", sessionId);
      log.debug("There are now {} connected clients", getNumConnectedClients());
    } else {
      log.warn("Client with session ID {} not removed from client manager as it was not found", sessionId);
    }
  }

  /**
   * Get the number of connected clients.
   *
   * @return The number of connected clients.
   */
  private int getNumConnectedClients() {
    return clients.size();
  }

  /**
   * Retrieve a client from the list of connected clients. {@code null} is returned if the client is not found.
   *
   * @param sessionId ID of the {@code WebSocketSession} associated with the client.
   * @return The requested client.
   */
  public ChatClient getClient(String sessionId) {
    ChatClient client = clients.get(sessionId);
    if (client == null) {
      log.warn("Client with session ID {} not found in list of connected clients", sessionId);
    }
    return client;
  }

  /**
   * Send an event to a specific client.
   *
   * @param sessionId The ID of the {@code WebSocketSession} associated with the client.
   * @param event     The event to send.
   */
  public void sendEvent(String sessionId, Event event) {
    ChatClient client;
    if ((client = getClient(sessionId)) == null) {
      log.warn("Event not sent to client with session ID {} as the client was not found", sessionId);
      return;
    }
    sendEventToSession(client.getSession(), event);
  }

  /**
   * Broadcast event to all connected clients.
   *
   * @param event The event to broadcast.
   */
  public void broadcastEvent(Event event) {
    clients.values().forEach(client -> sendEventToSession(client.getSession(), event));
  }

  /**
   * Send an event through a {@code WebSocketSession}.
   *
   * @param session The {@code WebSocketSession} to send the event through.
   * @param event   The event to send.
   */
  private void sendEventToSession(WebSocketSession session, Event event) {
    try {
      String eventJson = objectMapper.writeValueAsString(event);
      session.sendMessage(new TextMessage(eventJson));
      log.debug("Event of type \"{}\" sent to client with session ID of {}", event.getType().toString(), session.getId());
    } catch (JsonProcessingException e) {
      log.error("Failed to serialize event to JSON");
    } catch (IOException e) {
      log.error("Failed to send message to client with session ID {}", session.getId());
    }
  }

}
