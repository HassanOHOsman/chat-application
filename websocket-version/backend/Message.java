import java.util.UUID;

public class Message {
    private final String id;
    private String user;       
    private String content;
    private final long timestamp;
    private int likes;
    private int dislikes;

    
    public Message(String user, String content, long timestamp) {
        this.id = UUID.randomUUID().toString();
        this.user = user;
        this.content = content;
        this.timestamp = timestamp;
        this.likes = 0;
        this.dislikes = 0;
    }

    public Message(String user, String content) {
        this(user, content, System.currentTimeMillis());
    }

    public void like() {
        likes++;
    }

    public void dislike() {
        dislikes++;
    }


    public String getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getLikes() {
        return likes;
    }

    public int getDislikes() {
        return dislikes;
    }



    @Override
    public String toString() {
        return "[" + timestamp + "] " + user + ": " + content;
    }
}
