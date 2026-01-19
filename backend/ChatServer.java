import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {

    private static ChatLogic chatLogic = new ChatLogic();
    private static List<HttpExchange> waitingExchanges = new ArrayList<>();

    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

            server.createContext("/messages", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
            
                    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

                    try {
                        String method = exchange.getRequestMethod();

                        if (method.equalsIgnoreCase("OPTIONS")) {
                            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
                            exchange.sendResponseHeaders(204, -1);
                            return;
                        }

                        if (method.equalsIgnoreCase("POST")) {
                            String body = new String(exchange.getRequestBody().readAllBytes());
                            String user = body.split("\"user\":\"")[1].split("\"")[0];
                            String content = body.split("\"content\":\"")[1].split("\"")[0];

                            chatLogic.addMessage(user, content);

                            String jsonResponse = String.format(
                                    "{\"user\":\"%s\",\"content\":\"%s\",\"timestamp\":%d}",
                                    user, content, System.currentTimeMillis()
                            );

                            exchange.getResponseHeaders().add("Content-Type", "application/json");
                            exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
                            OutputStream os = exchange.getResponseBody();
                            os.write(jsonResponse.getBytes());
                            os.close();

                            List<HttpExchange> toRemove = new ArrayList<>();
                            for (HttpExchange waiting : waitingExchanges) {
                                try {
                                    waiting.getResponseHeaders().add("Content-Type", "application/json");
                                    waiting.sendResponseHeaders(200, jsonResponse.getBytes().length);
                                    OutputStream wOs = waiting.getResponseBody();
                                    wOs.write(jsonResponse.getBytes());
                                    wOs.close();
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                    toRemove.add(waiting);
                                }
                            }
                            waitingExchanges.removeAll(toRemove);
                            waitingExchanges.clear();

                        } 
                    
                        else if (method.equalsIgnoreCase("GET")) {
                            String query = exchange.getRequestURI().getQuery();
                            long since = 0;
                            if (query != null && query.startsWith("since=")) {
                                since = Long.parseLong(query.split("=")[1]);
                            }

                            List<Message> newMessages = chatLogic.newMessages(since);

                            if (!newMessages.isEmpty()) {
                                StringBuilder jsonArray = new StringBuilder("[");
                                for (int i = 0; i < newMessages.size(); i++) {
                                    Message m = newMessages.get(i);
                                    jsonArray.append(String.format(
                                            "{\"user\":\"%s\",\"content\":\"%s\",\"timestamp\":%d}",
                                            m.getUser(), m.getContent(), m.getTimestamp()
                                    ));
                                    if (i < newMessages.size() - 1) jsonArray.append(",");
                                }
                                jsonArray.append("]");

                                exchange.getResponseHeaders().add("Content-Type", "application/json");
                                exchange.sendResponseHeaders(200, jsonArray.toString().getBytes().length);
                                OutputStream os = exchange.getResponseBody();
                                os.write(jsonArray.toString().getBytes());
                                os.close();
                            } else {

                                waitingExchanges.add(exchange);

                                new Thread(() -> {
                                    try {
                                        Thread.sleep(30000); 
                                        if (waitingExchanges.remove(exchange)) {
                                            exchange.sendResponseHeaders(204, -1); 
                                            exchange.close();
                                        }
                                    } catch (Exception ignored) {}
                                }).start();
                            }
                        } 
                        else {
                            exchange.sendResponseHeaders(405, -1);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        exchange.sendResponseHeaders(500, -1);
                    }
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
