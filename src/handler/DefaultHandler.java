package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class DefaultHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange http) throws IOException {
        String error = "{\"error\": \"No route for " + http.getRequestMethod() + " " + http.getRequestURI() + ".\"}";
        http.getResponseHeaders().add("Content-Type", "application/json");
        http.sendResponseHeaders(404, error.getBytes().length);
        OutputStream os = http.getResponseBody();
        os.write(error.getBytes());
        http.close();
    }
}
