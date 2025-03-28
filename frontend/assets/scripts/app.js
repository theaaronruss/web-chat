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
webSocket.addEventListener('error', () => {
  showError('An unexpected error occurred');
});
webSocket.addEventListener('message', (event) => {
  const eventMessage = JSON.parse(event.data);
  if (eventMessage.eventName === 'error') {
    showError(eventMessage.content);
  } else if (eventMessage.eventName === 'log_in') {
    if (eventMessage.user === chosenUsername) {
      document.getElementById('login').remove();
    }
    const statusTemplate = document.getElementById('status-message-template');
    const statusElement = document.importNode(statusTemplate.content, true);
    statusElement.querySelector(
      '.message-content'
    ).textContent = `${eventMessage.user} has connected`;
    messagesList.append(statusElement);
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
