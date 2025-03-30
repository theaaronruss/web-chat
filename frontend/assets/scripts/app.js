const webSocket = new WebSocket('ws://localhost:8080/chat');
webSocket.addEventListener('open', function (event) {
  console.log('WebSocket connection established');
});
webSocket.addEventListener('close', function (event) {
  console.log('WebSocket connection closed');
});
webSocket.addEventListener('error', function (event) {
  console.error('WebSocket error:', event);
});
webSocket.addEventListener('message', function (event) {
  const eventData = JSON.parse(event.data);
  if (eventData.type === 'message') {
    const messageElement = document.importNode(
      document.getElementById('message-template').content,
      true
    );
    messageElement.querySelector('.message-author').textContent =
      eventData.username;
    messageElement.querySelector('.message-content').textContent =
      eventData.content;
    document.getElementById('message-list').appendChild(messageElement);
    document.querySelector('.message:last-child').scrollIntoView();
  }
});

document
  .getElementById('username-form')
  .addEventListener('submit', function (event) {
    event.preventDefault();
    document.getElementById('username-background').remove();
  });
