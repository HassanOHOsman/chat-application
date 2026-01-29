import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;



public class ChatServerWebSocket extends WebSocketServer {

    private Set<WebSocket> clients = Collections.synchronizedSet(new HashSet<>());

    public ChatServerWebSocket (int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket connection, ClientHandshake handshake) {
        clients.add(connection);
        System.out.println("New connection from " + connection.getRemoteSocketAddress());

    }

    @Override
    public void onClose(WebSocket connection, int code, String reason, boolean remote) {
        clients.remove(connection);
        System.out.println(connection.getRemoteSocketAddress() + " closed connection");

    }

    @Override
    public void onMessage(WebSocket connection, String message) {
        System.out.println("Recieved message: " + message);

        synchronized (clients) {
            for (WebSocket client : clients) {
                client.send(message);
            }
        }
    }

    @Override
    public void onError(WebSocket connection, Exception ex) {
        System.err.println("Error: " + ex.getMessage());
    }

    public static void main(String[] args) {

        int port 8081;
        ChatServerWebSocket server = new ChatServerWebSocket(port);
        server.start();
        
    }
}