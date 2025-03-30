# API

- [Overview](index.md)
- [API]()

## Overview

This WebSocket API enables real-time communication through JSON-encoded events. Clients must first send a `name` event to set their username before they can send messages. However, they can still receive certain events (`join`, `leave`, `message`, and `error`) without providing a username.

## Connection

To establish a WebSocket connection, clients should connect to the WebSocket server endpoint at `wss://<domain.com>/chat` (recommended for security). If using an insecure connection, use `ws://<domain.com>/chat`.

## Events

The API supports five event types: `name`, `message`, `join`, `leave`, and `error`. These JSON-encoded events facilitate communication between the frontend and backend. Each event type is described in detail below.

### `name` Event

**Direction:** Client → Server

Used to provide a username to the backend. This username is what is shown when sending messages and must be provided before you can send a message. If a client attempts to send a message without first sending a `name` event, the server will respond with an `error` event,

**Payload**

```
{
  "type": "name",
  "content": "johndoe"
}
```

**type:** The type of event (always "name" in this case)<br>
**content:** The username the client wants to use

The username must meet certain requirements:

1. Cannot be empty
2. Must be 15 characters or less
3. Can only contain alphanumeric characters (`A-Z`, `a-z`, `0-9`)

If the username does not meet these requirements, the server will respond with an `error` event, and the username will be rejected. The client must send a new `name` event with a valid username before being allowed to send messages. Once a username is accepted, all connected clients will receive a `join` event containing the new username. You can use this to tell when a username was accepted. At this point, you will be able to send messages.

### `message` Event

**Direction:**<br>
Client → Server<br>
Server → Client

Used to exchange messages between clients.

#### Client → Server

Clients send a `message` event to broadcast a message. If a client has not sent a valid `name` event or sends an empty content field, the message will not be sent.

**Payload**

```
{
  "type": "message",
  "content": "This is my message"
}
```

**type:** The type of event (always "message" in this case)<br>
**content:** The content of the message (must be non-empty)

#### Server → Client

The server sends a `message` event to all connected clients when a new message is received.

```
{
  "type": "message",
  "username": "johndoe",
  "content": "This is my message"
}
```

**type:** The type of event (always "message" in this case)<br>
**username:** The username of the author of the message<br>
**content:** The content of the message

### `join` Event

**Direction:** Server → Client

Sent when a new user successfully submits a username.

**Payload**

```
{
  "type": "join",
  "username": "johndoe"
}
```

**type:** The type of event (always "join" in this case)<br>
**username:** The username of the newly joined user

### `leave` Event

**Direction:** Server → Client

Sent when a user disconnects from the chat.

**Payload**

```
{
  "type": "leave",
  "username": "johndoe"
}
```

**type:** The type of event (always "leave" in this case)<br>
**username:** Username of the client who has disconnected

### `error` Event

**Direction:** Server → Client

Sent when an error occurs. Clients should use the message in the `content` field to understand the issue.

**Payload**

```
{
  "type": "error",
  "content": "You must provide a non-empty message for it to be sent"
}
```

**type:** The type of event (always "error" in this case)<br>
**content:** The error description
