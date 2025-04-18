package com.theaaronrussell.webchat.service;

import com.theaaronrussell.webchat.model.ChatClient;
import com.theaaronrussell.webchat.model.Event;
import com.theaaronrussell.webchat.util.ChatClientManager;
import com.theaaronrussell.webchat.util.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
public class ChatService {

  private static final Logger log = LoggerFactory.getLogger(ChatService.class);
  private static final int MAX_USERNAME_LENGTH = 15;
  private static final String USERNAME_REGEX = "^[a-zA-Z0-9]+$";
  private final ChatClientManager chatClientManager;

  @Autowired
  public ChatService(ChatClientManager chatClientManager) {
    this.chatClientManager = chatClientManager;
  }

  /**
   * Start keeping track of a newly connected client.
   *
   * @param session The {@code WebSocketSession} associated with the client.
   */
  public void connectClient(WebSocketSession session) {
    chatClientManager.addClient(session);
  }

  /**
   * Stop keeping track of a now disconnected client.
   *
   * @param session The {@code WebSocketSession} associated with the client.
   */
  public void disconnectClient(WebSocketSession session) {
    ChatClient client = chatClientManager.getClient(session.getId());
    chatClientManager.removeClient(session.getId());
    if (client == null || client.getUsername() == null) {
      log.debug("Leave event for client with session ID {} not broadcast as the client never set a username or was not found", session.getId());
    } else {
      broadcastLeaveEvent(client.getUsername());
    }
  }

  /**
   * Process any incoming events. This method does nothing if {@code event} is {@code null} or if an invalid event type is provided.
   *
   * @param originSessionId The ID of the {@code WebSocketSession} that the event originated from.
   * @param event           The event to be processed.
   */
  public void processEvent(String originSessionId, Event event) {
    if (event == null) {
      return;
    }
    switch (event.getType()) {
      case EventType.NAME -> {
        log.debug("Processing event for setting username of client with session ID {}", originSessionId);
        setUsername(originSessionId, event.getContent());
      }
      case EventType.MESSAGE -> {
        log.debug("Processing event for sending message from client with session ID {}", originSessionId);
        sendMessage(originSessionId, event.getContent());
      }
      default -> log.debug("Event from client with session ID {} not processed as it does not have a valid event type",
          originSessionId);
    }
  }

  /**
   * Set the username of a client. Does nothing if the client is not found in the list of connected clients or if the
   * client is already named.
   *
   * @param sessionId ID of the {@code WebSocketSession} associated with the client.
   * @param username  The username to use for the client.
   */
  private void setUsername(String sessionId, String username) {
    ChatClient client = chatClientManager.getClient(sessionId);
    if (client == null) {
      log.error("Username for client with session ID {} not set as it could not be found", sessionId);
      sendErrorEvent(sessionId, "An unexpected error prevented your username from being set");
    } else if (client.getUsername() != null) {
      log.info("Username for client with session ID {} not set as it already has a username", sessionId);
      sendErrorEvent(sessionId, "You cannot update your username after it has been set");
    } else if (username == null || username.isBlank()) {
      log.info("Username for client with session ID {} not set as the username is not provided or is blank", sessionId);
      sendErrorEvent(sessionId, "You must provide a non-empty username");
    } else if (username.length() > MAX_USERNAME_LENGTH) {
      log.info("Username for client with session ID {} not set as the username is longer than 15 characters", sessionId);
      sendErrorEvent(sessionId, "Username cannot be longer than 15 characters");
    } else if (!username.matches(USERNAME_REGEX)) {
      log.info("Username for client with session ID {} not set as the username contains non-alphanumeric characters", sessionId);
      sendErrorEvent(sessionId, "Username can only contain alphanumeric characters");
    } else if (chatClientManager.isUsernameTaken(username)) {
      log.info("Username for client with session ID {} not set as the username is already taken", sessionId);
      sendErrorEvent(sessionId, "Username is already taken");
    } else {
      client.setUsername(username);
      log.info("Username for client with session ID {} set to \"{}\"", sessionId, username);
      broadcastJoinEvent(username);
    }
  }

  /**
   * Broadcast a join event to notify connected clients of a now named client setting a username.
   *
   * @param username Username of the newly named client.
   */
  private void broadcastJoinEvent(String username) {
    log.debug("Broadcasting join event as \"{}\" has joined", username);
    Event outgoingEvent = new Event(EventType.JOIN, username, null);
    chatClientManager.broadcastEvent(outgoingEvent);
  }

  /**
   * Broadcast a leave event to notify connected clients of a client leaving/disconnecting.
   *
   * @param username Username of the leaving client.
   */
  private void broadcastLeaveEvent(String username) {
    log.debug("Broadcasting leave event as \"{}\" has left", username);
    Event outgoingEvent = new Event(EventType.LEAVE, username, null);
    chatClientManager.broadcastEvent(outgoingEvent);
  }

  /**
   * Send an error event to a specific client.
   *
   * @param sessionId ID of the {@code WebSocketSession} associated with the intended client.
   * @param message   The error message to be sent.
   */
  private void sendErrorEvent(String sessionId, String message) {
    log.debug("Sending error event to client with session ID {} with the message \"{}\"", sessionId, message);
    Event outgoingEvent = new Event(EventType.ERROR, null, message);
    chatClientManager.sendEvent(sessionId, outgoingEvent);
  }

  /**
   * Broadcast a message from a client to all other clients.
   *
   * @param originSessionId ID of the {@code WebSocketSession} associated with the client who sent the message.
   * @param message         The text content of the message to broadcast.
   */
  private void sendMessage(String originSessionId, String message) {
    ChatClient senderClient = chatClientManager.getClient(originSessionId);
    if (senderClient == null) {
      log.warn("Message from client with session ID {} not broadcast as the client could not be found", originSessionId);
      sendErrorEvent(originSessionId, "An unexpected error prevented your message from being sent");
    } else if (senderClient.getUsername() == null) {
      log.info("Message from client with session ID {} not broadcast as the client does not yet have a username", originSessionId);
      sendErrorEvent(originSessionId, "You must set a username before you can send a message");
    } else if (message == null || message.isBlank()) {
      log.info("Message from client with session ID {} not broadcast as the message is not provided or is blank", originSessionId);
      sendErrorEvent(originSessionId, "You must provide a non-empty message for it to be sent");
    } else {
      Event outgoingEvent = new Event(EventType.MESSAGE, senderClient.getUsername(), message);
      chatClientManager.broadcastEvent(outgoingEvent);
      log.info("{} says \"{}\"", senderClient.getUsername(), message);
    }
  }

}
