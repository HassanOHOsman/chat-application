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
  formattedMessage = formattedMessage.replace(/\*\*(.*?)\*\*/g, "<strong>$1</strong>");

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
    userMessage.innerHTML = `<strong>${message.user}:</strong> ${messageFormatter(message.content)}`;
    messageArea.append(userMessage);
  });
  messageArea.scrollTop = messageArea.scrollHeight;
}

// Function to add a message to state and update UI
function addMessage(message) {
  state.messages.push(message);
  displayMessages();
}

// Track the most recent message timestamp
let lastTimestamp = 0;

// Function to fetch new messages from the server (long-polling safe)
async function getNewMessages() {
  try {
    const response = await fetch(
      `http://localhost:8080/messages?since=${lastTimestamp}`
    );
    let newMessages = [];

    if (response.status !== 204) {
      newMessages = await response.json();
    }

    newMessages.forEach((message) => addMessage(message));

    if (newMessages.length > 0) {
      lastTimestamp = newMessages[newMessages.length - 1].timestamp;
    }

    // Poll again after 500ms
    setTimeout(getNewMessages, 0);
  } catch (err) {
    console.error("Unable to fetch new messages:", err);
    // Retry after 1 second if error occurs
    setTimeout(getNewMessages, 1000);
  }
}

// Load existing messages when the page opens
window.addEventListener("load", async () => {
  try {
    const response = await fetch("http://localhost:8080/messages?since=0");
    let storedMessages = [];

    if (response.status !== 204) {
      const data = await response.json();

      storedMessages = Array.isArray(data) ? data : [data];
    }

    storedMessages.forEach((message) => addMessage(message));

    if (storedMessages.length > 0) {
      lastTimestamp = storedMessages[storedMessages.length - 1].timestamp;
    }

    // Start polling for new messages
    getNewMessages();
  } catch (err) {
    console.error("Unable to retrieve messages:", err);
  }
});

// Handle send button click
sendButton.addEventListener("click", () => {
  let user = usernameInput.value.trim();
  let content = messageInput.value.trim();

  if (!user && !content) {
    alert("Please enter both a username and a message");
  } else if (!user) {
    alert("Please enter a username");
  } else if (!content) {
    alert("Please enter a message");
  } else {
    fetch("http://localhost:8080/messages", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ user, content }),
    })
      .then((response) => response.json())
      .then(() => {
        messageInput.value = "";
      })
      .catch((err) => console.error("Unable to send message:", err));
  }
});
