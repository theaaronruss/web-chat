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
  }
});

let chosenUsername;

document.getElementById('username-form').addEventListener('submit', (event) => {
  event.preventDefault();
  const usernameInput = document.getElementById('username-input');
  chosenUsername = usernameInput.value;
  sendUsername(usernameInput.value);
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
  const messageList = document.getElementById('message-list');
  messageList.appendChild(messageElement);
  messageList.querySelector('.message:last-child').scrollIntoView();
}
