const webSocket = new WebSocket('ws://localhost:8080/chat');
webSocket.addEventListener('message', (event) => {
  const eventData = JSON.parse(event.data);
  if (eventData.type === 'message') {
    addMessageToMessageList(eventData);
  } else if (
    eventData.type === 'join' &&
    eventData.username === chosenUsername
  ) {
    document.getElementById('username-form-background').remove();
    showJoinMessage(chosenUsername);
  } else if (eventData.type === 'join') {
    showJoinMessage(eventData.username);
  } else if (eventData.type === 'leave') {
    showLeaveMessage(eventData.username);
  }
});

let chosenUsername;
const messageList = document.getElementById('message-list');

document.getElementById('username-form').addEventListener('submit', (event) => {
  event.preventDefault();
  const usernameInput = document.getElementById('username-input');
  chosenUsername = usernameInput.value;
  sendUsername(chosenUsername);
});

document.getElementById('message-form').addEventListener('submit', (event) => {
  event.preventDefault();
  const messageInput = document.getElementById('message-input');
  const messageContent = messageInput.value;
  const outgoingEvent = {
    type: 'message',
    content: messageContent,
  };
  webSocket.send(JSON.stringify(outgoingEvent));
});

function sendUsername(username) {
  const outgoingEvent = {
    type: 'name',
    content: username,
  };
  webSocket.send(JSON.stringify(outgoingEvent));
}

function addMessageToMessageList(messageEvent) {
  const messageTemplate = document.getElementById('message-template');
  const messageElement = document.importNode(messageTemplate.content, true);
  messageElement.querySelector('.message-author').textContent =
    messageEvent.username;
  messageElement.querySelector('.message-content').textContent =
    messageEvent.content;
  messageList.appendChild(messageElement);
  messageList.querySelector('.message:last-child').scrollIntoView();
}

function showJoinMessage(username) {
  const joinMessageElement = document.createElement('p');
  joinMessageElement.className = 'join-message';
  joinMessageElement.textContent = `${username} has joined the chat`;
  messageList.appendChild(joinMessageElement);
  messageList.querySelector('.join-message:last-child').scrollIntoView();
}

function showLeaveMessage(username) {
  const leaveMessageElement = document.createElement('p');
  leaveMessageElement.className = 'join-message';
  leaveMessageElement.textContent = `${username} has left the chat`;
  messageList.appendChild(leaveMessageElement);
  messageList.querySelector('.join-message:last-child').scrollIntoView();
}
