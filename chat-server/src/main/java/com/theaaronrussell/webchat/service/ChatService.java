package com.theaaronrussell.webchat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theaaronrussell.webchat.dto.ChatClient;
import com.theaaronrussell.webchat.dto.ChatEvent;
import com.theaaronrussell.webchat.util.EventName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatService {

  private static final Logger log = LoggerFactory.getLogger(ChatService.class);
  private static final int USERNAME_MAX_LENGTH = 15;
  private final ConcurrentHashMap<String, ChatClient> clients = new ConcurrentHashMap<>();
  private final ObjectMapper objectMapper;

  @Autowired
  public ChatService(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

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
    String disconnectingUsername = clients.get(sessionId).getUsername();
    clients.remove(sessionId);
    ChatEvent outgoingEvent = new ChatEvent(EventName.DISCONNECT, disconnectingUsername, null);
    broadcastEvent(outgoingEvent);
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
      log.info("User {} has logged in", newUsername);
      ChatEvent outgoingEvent = new ChatEvent(EventName.LOG_IN, newUsername, null);
      broadcastEvent(outgoingEvent);
    }
  }

  /**
   * Send a message to all connected users of the server.
   *
   * @param sessionId ID of the session associated with the user sending the message.
   * @param event     Details of the message being sent.
   */
  public void sendMessage(String sessionId, ChatEvent event) {
    ChatClient sender = clients.get(sessionId);
    String senderUsername = sender.getUsername();
    String message = event.getContent();
    if (senderUsername == null) {
      log.error("User must log in before sending messages");
      return;
    }
    log.info("{} says \"{}\"", senderUsername, message);
    ChatEvent outgoingEvent = new ChatEvent(EventName.MESSAGE, senderUsername, message);
    broadcastEvent(outgoingEvent);
  }

  /**
   * Broadcast an event to all connected clients.
   *
   * @param event The event to broadcast.
   */
  private void broadcastEvent(ChatEvent event) {
    try {
      String eventJson = objectMapper.writeValueAsString(event);
      for (Map.Entry<String, ChatClient> recipient : clients.entrySet()) {
        WebSocketSession recipientSession = recipient.getValue().getSession();
        if (!recipientSession.isOpen()) {
          log.debug("Not sending message to closed session");
        } else if (recipient.getValue().getUsername() == null) {
          log.debug("Not sending message to logged out user");
        } else {
          recipientSession.sendMessage(new TextMessage(eventJson));
        }
      }
    } catch (JsonProcessingException e) {
      log.error("Failed to convert outgoing event to JSON");
    } catch (IOException e) {
      log.error("Failed to send message event to client");
    }
  }

}
