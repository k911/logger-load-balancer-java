package worker.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class ErrorHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String error =
                "{\"error\": \"No route for " + httpExchange.getRequestMethod() + " " + httpExchange.getRequestURI() +
                        ".\"}";
        httpExchange.getResponseHeaders().add("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(404, error.getBytes().length);
        OutputStream outputStream = httpExchange.getResponseBody();
        outputStream.write(error.getBytes());
        httpExchange.close();
    }
}