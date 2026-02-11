import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.nio.charset.StandardCharsets;

public class ChatServer {

    private static ChatLogic chatLogic = new ChatLogic();
    private static final List<HttpExchange> waitingExchanges = new ArrayList<>();

    private static void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }

    private static void usernameMiddleware(HttpExchange exchange) {
        String username = exchange.getRequestHeaders().getFirst("X-Username");
        exchange.setAttribute("username", username);
    }

    private static boolean messageMiddleware(HttpExchange exchange) throws IOException {

        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) return true;

        InputStream is = exchange.getRequestBody();
        String body = new String (is.readAllBytes(), StandardCharsets.UTF_8);

        body = body.trim();
        if (!body.startsWith("[") || !body.endsWith(("]"))) {
            exchange.sendResponseHeaders(400, -1);
            exchange.close();
            return false;
        }

        exchange.setAttribute("body", body);
        return true;
    }

    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

            server.createContext("/messages", exchange -> {
                addCorsHeaders(exchange);
                usernameMiddleware(exchange);

                if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                    boolean ok = messageMiddleware(exchange);
                    if (!ok) return;
                }

                String method = exchange.getRequestMethod();

                // Handle preflight OPTIONS request
                if (method.equalsIgnoreCase("OPTIONS")) {
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }

                // Handle POST requests
                if (method.equalsIgnoreCase("POST")) {

                    // Simple JSON parsing (fragile if message contains quotes)
                    String username = (String) exchange.getAttribute("username");
                    if (username == null || username.isEmpty()) {
                        String err = "{\"error\":\"Missing X-Username header\"}";
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(400, err.getBytes().length);
                        OutputStream os = exchange.getResponseBody();
                        os.write(err.getBytes());
                        os.close();
                        return;
                    }


                    String bodyRaw = (String) exchange.getAttribute("body");

                    if (bodyRaw.length() < 4) {
                        exchange.sendResponseHeaders(400, -1);
                        return;
                    }
                    
                    String content = bodyRaw.substring(2, bodyRaw.length() - 2);

                    chatLogic.addMessage(username, content);

                    String json = String.format(
                            "{\"user\":\"%s\",\"content\":\"%s\",\"timestamp\":%d}",
                            username, content, System.currentTimeMillis()
                    );

                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, json.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(json.getBytes());
                    os.close();

                    // Notify all waiting GET requests
                    for (HttpExchange waiting : waitingExchanges) {
                        try {
                            waiting.getResponseHeaders().set("Content-Type", "application/json");
                            waiting.sendResponseHeaders(200, json.getBytes().length);
                            OutputStream wos = waiting.getResponseBody();
                            wos.write(json.getBytes());
                            wos.close();
                        } catch (IOException ignored) {}
                    }
                    waitingExchanges.clear();
                    return;
                }

                // Handle GET requests
                if (method.equalsIgnoreCase("GET")) {
                    String query = exchange.getRequestURI().getQuery();
                    long since = 0;

                    if (query != null && query.startsWith("since=")) {
                        since = Long.parseLong(query.substring(6));
                    }

                    List<Message> newMessages = chatLogic.newMessages(since);
                    exchange.getResponseHeaders().set("Content-Type", "application/json");

                    if (!newMessages.isEmpty()) {
                        StringBuilder json = new StringBuilder("[");
                        for (int i = 0; i < newMessages.size(); i++) {
                            Message m = newMessages.get(i);
                            json.append(String.format(
                                    "{\"user\":\"%s\",\"content\":\"%s\",\"timestamp\":%d}",
                                    m.getUser(), m.getContent(), m.getTimestamp()
                            ));
                            if (i < newMessages.size() - 1) json.append(",");
                        }
                        json.append("]");

                        exchange.sendResponseHeaders(200, json.toString().getBytes().length);
                        OutputStream os = exchange.getResponseBody();
                        os.write(json.toString().getBytes());
                        os.close();
                    } else {
                        // Add to waiting exchanges for long-polling
                        waitingExchanges.add(exchange);

                        // Remove after 10 seconds if no new message
                        new Thread(() -> {
                            try {
                                Thread.sleep(10000);
                                if (waitingExchanges.remove(exchange)) {
                                    exchange.sendResponseHeaders(204, -1);
                                    exchange.close();
                                }
                            } catch (Exception ignored) {}
                        }).start();
                    }
                    return;
                }

                // Method not allowed
                exchange.sendResponseHeaders(405, -1);
            });

            server.start();
            System.out.println("Server running on http://localhost:8080/messages");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
