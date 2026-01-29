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




    public static void main(String[] args) {
        
    }
}