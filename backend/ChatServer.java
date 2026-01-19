import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;




public class ChatServer {
    
    private static ChatLogic chatLogic = new ChatLogic();
    public static void main(String[] args) {
        try {
    
    HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

    server.createContext("/messages", new HttpHandler() {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Hello from ChatServer!";
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    });

    server.setExecutor(null);
    server.start();
    System.out.println("Server running on http://localhost:8080");

} catch (IOException e) {
    e.printStackTrace();
}

    }
}

