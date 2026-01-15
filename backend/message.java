
public class Message {
    private String user;
    private String content;
    private long timestamp;

    public Message(String user, String content, long timestamp) {
        this.user = user;
        this.content = content;
        this.timestamp = timestamp;
    }
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }    
    
}
