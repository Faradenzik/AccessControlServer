package by.farad.accesscontrolserver;

import by.farad.accesscontrolserver.handlers.LoginHandler;
import by.farad.accesscontrolserver.handlers.RegisterHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class HttpServerApp {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/register", new RegisterHandler());
        server.createContext("/login", new LoginHandler());

        server.start();
        System.out.println("Server started on http://localhost:8080");
    }

    static class DefaultHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Access Control Server is running!";
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
