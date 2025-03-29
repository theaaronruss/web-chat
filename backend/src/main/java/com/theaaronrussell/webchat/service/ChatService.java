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
    if (event.getType() == EventType.NAME) {
      log.debug("Processing event for setting username of client with session ID {}", originSessionId);
      setUsername(originSessionId, event.getContent());
    } else {
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
  }

}
