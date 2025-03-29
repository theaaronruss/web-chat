package com.theaaronrussell.webchat.util;

import com.theaaronrussell.webchat.model.ChatClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class for keeping track of the connected chat clients. Allows sending events to individual clients as well
 * as broadcasting to all.
 */
@Component
public class ChatClientManager {

  private static final Logger log = LoggerFactory.getLogger(ChatClientManager.class);
  private final ConcurrentHashMap<String, ChatClient> sessions = new ConcurrentHashMap<>();

  /**
   * Add a chat client to manage. The username by default is {@code null}.
   *
   * @param webSocketSession The {@code WebSocketSession} associated with the chat client.
   * @return The ID of the {@code webSocketSession}.
   */
  public String addClient(WebSocketSession webSocketSession) {
    ChatClient client = new ChatClient(null, webSocketSession);
    String sessionId = webSocketSession.getId();
    sessions.put(sessionId, client);
    log.debug("Client with session ID {} added to client manager", sessionId);
    return sessionId;
  }

  /**
   * Remove a client from the list of managed chat clients. This does not close the {@code
   * WebSocketSession} associated with the client.
   *
   * @param sessionId ID of the {@code WebSocketSession} associated with the chat client.
   */
  public void removeClient(String sessionId) {
    if (sessions.remove(sessionId) != null) {
      log.debug("Client with session ID {} removed from client manager", sessionId);
    } else {
      log.warn("Client with session ID {} not removed from client manager because it was not found", sessionId);
    }
  }

}
