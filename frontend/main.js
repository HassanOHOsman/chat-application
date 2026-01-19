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

// Create container to display all messsages from all users 
const messageArea = document.createElement("div");
messageArea.id = "message-area";
rootContainer.append(messageArea);


// Create a state object, acting as a memoery, keep track of all messages and updating UI with new messages
const state = {
    messages: []
}

// Create a function to display messages sent through send button
function displayMessages() {
    messageArea.innerHTML = "";
    state.messages.forEach(message => {
        const userMessage = document.createElement("p");
        userMessage.textContent = `${message.user}: ${message.content}`;
        messageArea.append(userMessage);
        
    });

    messageArea.scrollTop = messageArea.scrollHeight;
}

// Track the most recent message
let lastTimestamp = 0;

function getNewMessages() {
    fetch(`http://localhost:8080/messages?since=${lastTimestamp}`)
        .then(response => response.json())
        .then(newMessage => {
            newMessage.forEach(message => addMessage(message));

            if (newMessage.length > 0) {
                lastTimestamp = newMessage[newMessage.length - 1].timestamp;
            }

            setTimeout(getNewMessages, 500);
        })
        .catch(error => {
            console.error("Unable to fetch new messages:", error);

            setTimeout(getNewMessages, 1000);
        });
}

// Build GET request to retrieve messages from server when the chat app is open
window.addEventListener("load", () => {
    fetch("http://localhost:8080/messages", { method: "GET" })
      .then((response) => response.json())
      .then((storedMessages) =>
        storedMessages.forEach((message) => addMessage(message))
      )
      .catch((err) => console.error("Unable to retrieve message:", err));

      getNewMessages();

})


// Handle the send button click to display user and message
sendButton.addEventListener("click", () => {
    let user = usernameInput.value.trim();
    let content = messageInput.value.trim();

    if (!user && !content) {
        alert("Please enter both a username and a message");  
    } else if (!user) {
        alert("Please enser a username");
    } else if (!content) {
        alert("Please enter a message");
    } else {
        fetch("http://localhost:8080/messages", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            user: user,
            content: content,
          }),
        })
          .then((response) => response.json())
          .then((newMessage) => addMessage(newMessage))
          .catch((err) => console.error("Unable to send message:", err));
    }
})

// Create a function to add messages from other users too and update UI with all messages
function addMessage(message) {
    state.messages.push(message);
    displayMessages();
}