package ServerLoadBalancer.src.handler;

import ServerLoadBalancer.src.items.HttpException;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

public class HttpExceptionHandler {
    private Gson gson;

    public HttpExceptionHandler(Gson gson) {
        this.gson = gson;
    }

    public void handle(HttpException exception, HttpExchange http) throws IOException {
        String serializedError = this.gson.toJson(exception);
        http.getResponseHeaders().add("Content-Type", "application/json");
        http.sendResponseHeaders(exception.getStatusCode(), serializedError.getBytes().length);
        OutputStream os = http.getResponseBody();
        os.write(serializedError.getBytes());
    }
}
