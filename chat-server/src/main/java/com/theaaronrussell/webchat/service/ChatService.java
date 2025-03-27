package com.theaaronrussell.webchat.service;

import com.theaaronrussell.webchat.dto.ChatClient;
import com.theaaronrussell.webchat.dto.ChatEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatService {

  private static final Logger log = LoggerFactory.getLogger(ChatService.class);
  private final ConcurrentHashMap<String, ChatClient> clients = new ConcurrentHashMap<>();

  /**
   * Start keeping track of new client.
   *
   * @param session The {@code WebSocketSession} associated with the new client.
   */
  public void acceptNewClient(WebSocketSession session) {
    clients.put(session.getId(), new ChatClient(session));
  }

  /**
   * Stop tracking disconnected client.
   *
   * @param sessionId ID of the {@code WebSocketSession} to stop tracking.
   */
  public void disconnectClient(String sessionId) {
    clients.remove(sessionId);
  }

  public void sendMessage(String sessionId, ChatEvent event) {
    ChatClient client = clients.get(sessionId);
    if (client.getUsername() == null) {
      log.error("User must log in before sending messages");
      return;
    }
    log.info("{}: {}", client.getUsername(), event.getContent());
    // TODO: Broadcast message to all clients
  }

}
