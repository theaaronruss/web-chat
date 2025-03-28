const serverUrl = 'ws://localhost:8080/chat';

const messagesList = document.getElementById('messages-list');
const messageInput = document.getElementById('message-input');
const messageSendButton = document.getElementById('message-send-button');
const statusMessageTemplate = document.getElementById(
  'status-message-template'
);
const messageTemplate = document.getElementById('message-template');
const webSocket = new WebSocket(serverUrl);

messageSendButton.addEventListener('click', () => {
  const message = messageInput.value;
  messageInput.value = messageInput.getAttribute('value');
  sendMessage(message);
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
webSocket.addEventListener('message', () => {});

function sendMessage(message) {
  console.log('Sending message:', message);
}
