const serverUrl = 'ws://localhost:8080/chat';

const messagesList = document.getElementById('messages-list');
const messageInput = document.getElementById('message-input');
const messageSendButton = document.getElementById('message-send-button');
const usernameSubmitButton = document.getElementById('username-submit-button');
const statusMessageTemplate = document.getElementById(
  'status-message-template'
);
const messageTemplate = document.getElementById('message-template');
const webSocket = new WebSocket(serverUrl);
let chosenUsername = '';

messageSendButton.addEventListener('click', () => {
  const message = messageInput.value;
  messageInput.value = messageInput.getAttribute('value');
  sendMessage(message);
});
usernameSubmitButton.addEventListener('click', () => {
  const username = document.getElementById('username-input').value;
  sendLogInEvent(username);
});
webSocket.addEventListener('open', () => {
  console.log('WebSocket connected');
});
webSocket.addEventListener('close', () => {
  console.log('WebSocket disconnected');
});
webSocket.addEventListener('error', (event) => {
  console.log('WebSocket error:', event);
});
webSocket.addEventListener('message', (event) => {
  const eventMessage = JSON.parse(event.data);
  if (eventMessage.eventName === 'error') {
    showError(eventMessage.content);
  } else if (
    eventMessage.eventName === 'log_in' &&
    eventMessage.user === chosenUsername
  ) {
    document.getElementById('login').remove();
  }
});

function sendLogInEvent(username) {
  chosenUsername = username;
  const event = {
    eventName: 'log_in',
    content: username,
  };
  webSocket.send(JSON.stringify(event));
}

function sendMessage(message) {
  console.log('Sending message:', message);
}

function showError(errorMessage) {
  const errorTemplate = document.getElementById('error-template');
  const errorElement = document.importNode(errorTemplate.content, true);
  errorElement.querySelector('p').textContent = errorMessage;
  document.body.appendChild(errorElement);
  setTimeout(() => {
    const errorElement = document.querySelector('.error');
    errorElement.remove();
  }, 3000);
}
