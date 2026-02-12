import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;



public class ChatServerWebSocket extends WebSocketServer {

    private ChatLogic chatLogic = new ChatLogic();
    private Set<WebSocket> clients = Collections.synchronizedSet(new HashSet<>());

    public ChatServerWebSocket (int port) {
        super(new InetSocketAddress("0.0.0.0", port));
    }

    @Override
    public void onStart() {
        System.out.println("Websocket server started on: " + getPort());
    }

    @Override
    public void onOpen(WebSocket connection, ClientHandshake handshake) {
        clients.add(connection);
        System.out.println("New connection from " + connection.getRemoteSocketAddress());

        Map<String, Object> historyMessage = new HashMap<>();
        historyMessage.put("type", "history");
        historyMessage.put("payload", chatLogic.getAllMessages());

        String json = new Gson().toJson(historyMessage);
        connection.send(json);

    }

    @Override
    public void onClose(WebSocket connection, int code, String reason, boolean remote) {
        clients.remove(connection);
        System.out.println(connection.getRemoteSocketAddress() + " closed connection");

    }

    @Override
    public void onMessage(WebSocket connection, String messageJson) {
        System.out.println("Recieved message: " + messageJson);

        Gson gson = new Gson();
        Map<String, Object> data = gson.fromJson(messageJson, Map.class);

        String type = (String) data.get("type");

        if ("message".equals(type)) {
            String user = (String) data.get("user");
            String content = (String) data.get("content");

            chatLogic.addMessage(user, content);

            Message saved = chatLogic.getAllMessages()
                .get(chatLogic.getAllMessages().size() - 1);

            Map<String, Object> outgoing = new HashMap<>();
            outgoing.put("type", "new-message");
            outgoing.put("payload", saved);

            String outgoingJson = gson.toJson(outgoing);

            synchronized (clients) {
                for (WebSocket client : clients) {
                    client.send(outgoingJson);
                }
            }

        } else if ("reaction".equals(type)) {
            String messageId = (String) data.get("messageId");
            String reaction = (String) data.get("reaction");

            Message updatedMessage = null;

            if ("like".equals(reaction)) {
                updatedMessage = chatLogic.likeMessage(messageId);
            } else if ("dislike".equals(reaction)) {
                updatedMessage = chatLogic.dislikeMessage(messageId);
            }

            if (updatedMessage != null) {
                Map<String, Object> payload = new HashMap<>();
                payload.put("messageId", updatedMessage.getId());
                payload.put("likes", updatedMessage.getLikes());
                payload.put("dislikes", updatedMessage.getDislikes());

                Map<String, Object> outgoing = new HashMap<>();
                outgoing.put("type", "reaction-update");
                outgoing.put("payload", payload);

                String outgoingJson = gson.toJson(outgoing);

                synchronized (clients) {
                    for (WebSocket client : clients) {
                        client.send(outgoingJson);
                    }
                }
            }

        }

    }

    @Override
    public void onError(WebSocket connection, Exception ex) {
        System.err.println("Error: " + ex.getMessage());
    }

    public static void main(String[] args) {

        int port = 8081;
        ChatServerWebSocket server = new ChatServerWebSocket(port);
        server.start();
        
    }
}