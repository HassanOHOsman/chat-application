// Create root container to hold all UI elements
const rootContainer = document.createElement("div");
rootContainer.id = "chat-app";
document.body.append(rootContainer);

// Create username input 
const usernameInput = document.createElement("input");
usernameInput.placeholder = "Enter your username";
rootContainer.append(usernameInput);

// Create textarea to add messsages 
const messageInput = document.createElement("textarea");
messageInput.placeholder = "Enter your messages";
rootContainer.append(messageInput);

// Create button to send messsages 
const sendButton = document.createElement("button");
sendButton.textContent = "Send";
rootContainer.append(sendButton);

