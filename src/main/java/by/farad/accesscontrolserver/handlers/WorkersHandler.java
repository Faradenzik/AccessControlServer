package by.farad.accesscontrolserver.handlers;

import by.farad.accesscontrolserver.db.DatabaseManager;
import by.farad.accesscontrolserver.util.ObjectMapperSingleton;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class WorkersHandler implements HttpHandler {
    private final ObjectMapper objectMapper = ObjectMapperSingleton.getObjectMapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            try {
                List<Map<String, Object>> workers = DatabaseManager.getWorkersList();

                String response = objectMapper.writeValueAsString(workers);
                exchange.getResponseHeaders().set("Content-Type", "application/json");

                exchange.sendResponseHeaders(200, response.getBytes().length);

                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } catch (SQLException e) {
                String response = "{\"error\": \"Database error: " + e.getMessage() + "\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(500, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        } else {
            String response = "{\"error\": \"Invalid request method\"}";
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(405, response.getBytes().length); // 405 Method Not Allowed
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
