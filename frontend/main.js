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
}

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
        state.messages.push({
            user: user,
            content: content,
            });
            messageInput.value = "";
            displayMessages(); 
    }
})

// Create a function to add messages from other users too and update UI with all messages
function addMessage(message) {
    state.messages.push(message);
    displayMessages();
}