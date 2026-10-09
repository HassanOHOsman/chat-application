# QuickChat: Real-Time Chat Application

A client-server messaging application that lets multiple users chat in real time. A Java backend and a JavaScript frontend communicate over WebSockets, so messages appear instantly without refreshing the page.

**Live demo:** [QuickChat](https://hassanohosman-chat-app-websocket-frontend.trainees.hosting.cyf.academy/)

## Features

- **Real-time messaging:** messages are delivered instantly to all connected users
- **Multiple concurrent users:** several clients can connect and chat at the same time
- **Usernames and timestamps:** each message shows who sent it and when
- **Text formatting:** bold, italic and underline options for messages
- **Message reactions:** thumbs up / thumbs down buttons with counters on each message

## Tech Stack

| Area | Technology |
|------|------------|
| Backend | Java |
| Frontend | JavaScript, HTML, CSS |
| Communication | WebSockets |
| Version control | Git / GitHub |

## How It Works

1. A user opens the frontend and enters a username and message.
2. The browser opens a persistent WebSocket connection to the Java server.
3. When a message is sent, the server receives it and broadcasts it to every connected client.
4. Each client updates its chat window as soon as the message arrives, with no polling or page reloads.

Unlike a standard HTTP request-response model, a WebSocket keeps one connection open, so the server can push messages to clients at any time. This keeps latency low.





