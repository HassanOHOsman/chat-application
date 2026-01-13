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

// Create a function to display all messages
function displayMessages() {
    messageArea.innerHTML = "";
    state.messages.forEach(message => {
        const userMessage = document.createElement("p");
        userMessage.textContent = `${message.user}: ${message.content}`;
        messageArea.append(userMessage);
    });
}

// Handle the send button click to display user and message
sendButton.addEventListener("click", () => {
    const user = usernameInput.value;
    const content = messageInput.value;
    messages.push(content);
    content = "";
    displayMessages();
})
