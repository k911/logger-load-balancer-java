package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import items.HttpException;
import items.Worker;
import repository.WorkerRepository;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

public class WorkerFirstHttpHandler implements HttpHandler {

    private HttpExceptionHandler exceptionHandler;
    private DefaultHttpHandler defaultHandler;
    private WorkerRepository workerRepository;
    private Gson gson;

    public WorkerFirstHttpHandler(HttpExceptionHandler exceptionHandler, DefaultHttpHandler defaultHandler, WorkerRepository workerRepository, Gson gson) {
        this.exceptionHandler = exceptionHandler;
        this.defaultHandler = defaultHandler;
        this.workerRepository = workerRepository;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange http) throws IOException {
        if (!http.getRequestURI().getPath().equals("/workers/first")) {
            this.defaultHandler.handle(http);
        }

        try {
            switch (http.getRequestMethod()) {
                case "GET":
                    this.getFirstWorker(http);
                    break;
                default:
                    throw new HttpException("Method " + http.getRequestMethod() + " is not supported.", 405);
            }
        } catch (HttpException e) {
            this.exceptionHandler.handle(e, http);
        }

        http.close();
    }

    private void getFirstWorker(HttpExchange http) throws IOException, HttpException {
        Worker worker = workerRepository.first();

        if(null == worker) {
            throw new HttpException("No workers set yet.", 404);
        }

        String serializedWorker = this.gson.toJson(worker);

        http.getResponseHeaders().add("Content-Type", "application/json");
        http.sendResponseHeaders(200, serializedWorker.getBytes().length);
        OutputStream os = http.getResponseBody();
        os.write(serializedWorker.getBytes());
    }
}
