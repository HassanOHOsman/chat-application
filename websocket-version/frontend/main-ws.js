// Create root container to hold all UI elements
const rootContainer = document.createElement("div");
rootContainer.id = "chat-app";
document.body.append(rootContainer);

// Create title for the chat app
const appTitle = document.createElement("h1");
appTitle.textContent = "QuickChat";
appTitle.id = "app-title";
rootContainer.append(appTitle);

// Create username input
const usernameInput = document.createElement("input");
usernameInput.placeholder = "Enter your username";
rootContainer.append(usernameInput);

// Create textarea to add messages
const messageInput = document.createElement("textarea");
messageInput.placeholder = "Enter your message";
rootContainer.append(messageInput);

// Create button to send messages
const sendButton = document.createElement("button");
sendButton.textContent = "Send";
rootContainer.append(sendButton);

// Create button to make words bold
const boldButton = document.createElement("button");
boldButton.id = "bold-btn";
boldButton.textContent = "B";

// Create button to make words italic
const italicButton = document.createElement("button");
italicButton.id = "italic-btn";
italicButton.textContent = "𝑰";

// Create button to make words underlined
const underlinedButton = document.createElement("button");
underlinedButton.id = "underline-btn";
underlinedButton.textContent = "U̲";

// Create text-formatting toolbar
const textFormatBar = document.createElement("div");
textFormatBar.id = "format-bar";

textFormatBar.append(boldButton);
textFormatBar.append(italicButton);
textFormatBar.append(underlinedButton);

rootContainer.append(textFormatBar);

// Create container to display all messages from all users
const messageArea = document.createElement("div");
messageArea.id = "message-area";
rootContainer.append(messageArea);

// State object to keep track of messages
const state = {
  messages: [],
};

const socket = new WebSocket("ws://localhost:8081");

socket.onopen = () => {
  console.log("Connected to websocket server");
}

socket.onclose = () => {
  console.log("Disconnected from websocket server");
};

socket.onerror = (err) => {
  console.error("Error: ", err);
};  

socket.onmessage = (event) => {
  const message = JSON.parse(event.data);
  addMessage(message);
};



// UI helper for user so that words are made bold, italic or underlined
boldButton.addEventListener("click", () => formatSelectedWord("**"));
italicButton.addEventListener("click", () => formatSelectedWord("*"));
underlinedButton.addEventListener("click", () => formatSelectedWord("__"));

function formatSelectedWord(wrapper) {
  const start = messageInput.selectionStart;
  const end = messageInput.selectionEnd;
  if (start == end) return;

  const text = messageInput.value;
  const selected = text.slice(start, end);

  const before = text.slice(0, start);
  const after = text.slice(end);

  messageInput.value = before + wrapper + selected + wrapper + after;

  // Put cursor after the newly added wrapper
  messageInput.selectionStart = start + wrapper.length;
  messageInput.selectionEnd = end + wrapper.length;
  messageInput.focus();
}

// Build a function that format message (option to make some words bold, italic, or underlined)

function messageFormatter(text) {
  let formattedMessage = text;

  //Make bold
  formattedMessage = formattedMessage.replace(
    /\*\*(.*?)\*\*/g,
    "<strong>$1</strong>"
  );

  // Make italic
  formattedMessage = formattedMessage.replace(/\*(.*?)\*/g, "<em>$1</em>");

  //Make underlined
  formattedMessage = formattedMessage.replace(/__(.*?)__/g, "<u>$1</u>");

  return formattedMessage;
}

// Function to display all messages
function displayMessages() {
  messageArea.innerHTML = "";
  state.messages.forEach((message) => {
    const userMessage = document.createElement("p");
    userMessage.innerHTML = `<strong>${message.user}:</strong> [${message.timestamp}]: ${messageFormatter(message.content)}`;
    messageArea.append(userMessage);
  });
  messageArea.scrollTop = messageArea.scrollHeight;
}

// Function to add a message to state and update UI
function addMessage(message) {
  state.messages.push(message);
  displayMessages();
}



// Handle send button click
sendButton.addEventListener("click", () => {
  let user = usernameInput.value.trim();
  let content = messageInput.value.trim();

  if (!user && !content) {
    alert("Please enter both a username and a message");
    return;
  } else if (!user) {
    alert("Please enter a username");
    return;
  } else if (!content) {
    alert("Please enter a message");
    return;
  } 

  const message = {
    user,
    content,
    timestamp: new Date().toLocaleTimeString()
  };

  socket.send(JSON.stringify(message));
  messageInput.value = "";
});
