// Message.java
public class Message {
    private String sender;
    private String content;
    private long timestamp;

    // Constructor
    public Message(String sender, String content) {
        this.sender = sender;
        this.content = content;
        this.timestamp = System.currentTimeMillis(); // time in milliseconds
    }

    // Getters
    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // Setters (if you want messages to be mutable)
    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // Optional: override toString for easier logging
    @Override
    public String toString() {
        return "[" + timestamp + "] " + sender + ": " + content;
    }
}

