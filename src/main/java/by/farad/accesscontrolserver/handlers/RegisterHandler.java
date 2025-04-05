package by.farad.accesscontrolserver.handlers;

import by.farad.accesscontrolserver.util.PasswordUtil;
import by.farad.accesscontrolserver.db.DatabaseManager;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Map;

public class RegisterHandler implements HttpHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            // Чтение данных из запроса
            InputStream inputStream = exchange.getRequestBody();
            Map<String, String> requestBody = objectMapper.readValue(inputStream, Map.class);

            String login = requestBody.get("login");
            String password = requestBody.get("password");

            // Хэширование пароля
            String hashedPassword = PasswordUtil.hashPassword(password);

            try {
                DatabaseManager.saveUserToDatabase(login, hashedPassword);

                String response = "{\"message\": \"User registered successfully!\"}";
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
        }
    }
}
