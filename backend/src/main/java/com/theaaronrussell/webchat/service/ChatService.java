package com.theaaronrussell.webchat.service;

import com.theaaronrussell.webchat.model.Event;
import com.theaaronrussell.webchat.util.ChatClientManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

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
    chatClientManager.broadcastEvent(event);
  }

}
