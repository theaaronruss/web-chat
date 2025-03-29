package com.theaaronrussell.webchat.service;

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
      chatClientManager.setUsername(originSessionId, event.getContent());
    }
  }

}
