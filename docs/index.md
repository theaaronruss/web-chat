# Web Chat

- [Overview]()
- [API](api.md)

## Introduction

Web Chat is a real-time chat application that utilizes WebSockets for communication. The backend is built with Java (Spring Boot), while the frontend is developed using HTML, CSS, and JavaScript. For a detailed breakdown of the project’s functionality, refer to the provided documentation.

## Technical Details

Web Chat operates by exchanging JSON-encoded "events" between the frontend and backend. An example of such an event is shown below:

```
{
  "type": "message",
  "username": "johndoe",
  "content": "Hello, world!"
}
```

Each event includes a `type` field, which specifies the type of action to perform when the event is received. The example above is a `message` event being sent from the server to the client. More information about the different event types and their functionalities can be found on the [API](api.md) details page.
