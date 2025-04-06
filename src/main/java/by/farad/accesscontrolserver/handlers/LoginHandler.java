package by.farad.accesscontrolserver.handlers;

import by.farad.accesscontrolserver.db.DatabaseManager;
import by.farad.accesscontrolserver.db.EventLogger;
import by.farad.accesscontrolserver.util.ObjectMapperSingleton;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Map;

public class LoginHandler implements HttpHandler {
    private static final ObjectMapper objectMapper = ObjectMapperSingleton.getObjectMapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            // Чтение данных из запроса
            InputStream inputStream = exchange.getRequestBody();
            Map<String, String> requestBody = objectMapper.readValue(inputStream, Map.class);

            String login = requestBody.get("login");
            String password = requestBody.get("password");

            try {
                boolean isAuthenticated = DatabaseManager.checkUserCredentials(login, password);

                String response;
                if (isAuthenticated) {
                    response = "{\"message\": \"Успешная авторизация\"}";
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, response.getBytes().length);

                    EventLogger.logEvent("Авторизация", "Пользователь вошел в систему", login);
                } else {
                    response = "{\"error\": \"Неверные данные\"}";
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(401, response.getBytes().length);
                }

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
        }
    }
}
