package com.theaaronrussell.webchat.service;

import com.theaaronrussell.webchat.model.ChatClient;
import com.theaaronrussell.webchat.model.Event;
import com.theaaronrussell.webchat.util.ChatClientManager;
import com.theaaronrussell.webchat.util.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

  private static final Logger log = LoggerFactory.getLogger(ChatService.class);
  private final ChatClientManager chatClientManager;

  @Autowired
  public ChatService(ChatClientManager chatClientManager) {
    this.chatClientManager = chatClientManager;
  }

  /**
   * Process any incoming events.
   *
   * @param originSessionId The ID of the {@code WebSocketSession} that the event originated from.
   * @param event           The event to be processed.
   */
  public void processEvent(String originSessionId, Event event) {
    switch (event.getType()) {
      case EventType.NAME -> {
        log.debug("Processing event for setting username of client with session ID {}", originSessionId);
        setUsername(originSessionId, event.getContent());
      }
      case EventType.MESSAGE -> {
        log.debug("Processing event for sending message from client with session ID {}", originSessionId);
        sendMessage(originSessionId, event.getContent());
      }
      default ->
          log.warn("Event from client with session ID {} not processed as it has an unknown event type", originSessionId);
    }
  }

  /**
   * Set the username of a client. Does nothing if the client is not found in the list of connected clients or if the
   * client is already named.
   *
   * @param sessionId ID of the {@code WebSockcetSession} associated with the client.
   * @param username  The username to use for the client.
   */
  private void setUsername(String sessionId, String username) {
    ChatClient client = chatClientManager.getClient(sessionId);
    if (client == null) {
      log.warn("Username for client with session ID {} not set as it could not be found", sessionId);
      return;
    }
    if (client.getUsername() != null) {
      log.warn("Username for client with session ID {} not set as it already has a username", sessionId);
      return;
    }
    client.setUsername(username);
    log.info("Username for client with session ID {} update to {}", sessionId, username);
    broadcastJoinEvent(username);
  }

  /**
   * Broadcast a join event to notify connected clients of a now named client setting a username.
   *
   * @param username Username of the newly named client.
   */
  private void broadcastJoinEvent(String username) {
    Event outgoingEvent = new Event(EventType.JOIN, username, null);
    chatClientManager.broadcastEvent(outgoingEvent);
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
      return;
    }
    if (senderClient.getUsername() == null) {
      log.warn("Message from client with session ID {} not broadcast as the client does not yet have a username", originSessionId);
      return;
    }
    if (message == null || message.isBlank()) {
      log.warn("Message from client with session ID {} not broadcast as the message is not provided or is blank", originSessionId);
    }
    Event outgoingEvent = new Event(EventType.MESSAGE, senderClient.getUsername(), message);
    chatClientManager.broadcastEvent(outgoingEvent);
  }

}
