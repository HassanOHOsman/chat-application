// Message.java
public class Message {
    private String user;       // matches your ChatServer expectations
    private String content;
    private long timestamp;

    // Constructor with all three parameters
    public Message(String user, String content, long timestamp) {
        this.user = user;
        this.content = content;
        this.timestamp = timestamp;
    }

    // Optional: constructor without timestamp (auto-set to now)
    public Message(String user, String content) {
        this(user, content, System.currentTimeMillis());
    }

    // Getters
    public String getUser() {
        return user;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // Setters if needed
    public void setUser(String user) {
        this.user = user;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // Optional: easier logging
    @Override
    public String toString() {
        return "[" + timestamp + "] " + user + ": " + content;
    }
}
