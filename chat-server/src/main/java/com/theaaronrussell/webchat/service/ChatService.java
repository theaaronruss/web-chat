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
  private static final int USERNAME_MAX_LENGTH = 15;
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

  /**
   * "Log in" a user by setting their username.
   *
   * @param sessionId ID of the session associated with the user. Does nothing if user is already logged in, username
   *                  is blank, or if the username is too long.
   * @param event     Details related to the log in event.
   */
  public void logIn(String sessionId, ChatEvent event) {
    ChatClient client = clients.get(sessionId);
    String newUsername = event.getContent();
    if (client.getUsername() != null) {
      log.error("User is already logged in");
    } else if (newUsername.isBlank()) {
      log.error("Username cannot be blank");
    } else if (newUsername.length() > USERNAME_MAX_LENGTH) {
      log.error("Username cannot be longer than {} characters", USERNAME_MAX_LENGTH);
    } else if (!newUsername.matches("^[a-zA-Z0-9]+$")) {
      log.error("Username must only contain alphanumeric characters");
    } else {
      client.setUsername(event.getContent());
    }
  }

  /**
   * Send a message to all connected users of the server.
   *
   * @param sessionId ID of the session associated with the user sending the message.
   * @param event     Details of the message being sent.
   */
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
