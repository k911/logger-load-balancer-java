package ServerLoadBalancer.src.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ServerLoadBalancer.src.items.HttpException;
import ServerLoadBalancer.src.items.Worker;
import ServerLoadBalancer.src.repository.WorkerRepository;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

public class WorkersHttpHandler implements HttpHandler {

    private HttpExceptionHandler exceptionHandler;
    private DefaultHttpHandler defaultHandler;
    private WorkerRepository workerRepository;
    private Gson gson;

    public WorkersHttpHandler(HttpExceptionHandler exceptionHandler, DefaultHttpHandler defaultHandler, WorkerRepository workerRepository, Gson gson) {
        this.exceptionHandler = exceptionHandler;
        this.defaultHandler = defaultHandler;
        this.workerRepository = workerRepository;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange http) throws IOException {
        if (!http.getRequestURI().getPath().equals("/workers")) {
            this.defaultHandler.handle(http);
        }

        try {
            switch (http.getRequestMethod()) {
                case "POST":
                    this.registerWorker(http);
                    break;
                case "GET":
                    this.getAllWorkers(http);
                    break;
                default:
                    throw new HttpException("Method " + http.getRequestMethod() + " is not supported.", 405);
            }
        } catch (HttpException e) {
            this.exceptionHandler.handle(e, http);
        }

        http.close();
    }

    private void getAllWorkers(HttpExchange http) throws IOException, HttpException {
        Collection<Worker> workers = workerRepository.findAll();
        String serializedWorkers = this.gson.toJson(workers);

        http.getResponseHeaders().add("Content-Type", "application/json");
        http.sendResponseHeaders(200, serializedWorkers.getBytes().length);
        OutputStream os = http.getResponseBody();
        os.write(serializedWorkers.getBytes());
    }

    private void registerWorker(HttpExchange http) throws IOException, HttpException {
        Reader reader = new BufferedReader(new InputStreamReader(http.getRequestBody(), Charset.forName(StandardCharsets.UTF_8.name())));
        Worker worker = gson.fromJson(reader, Worker.class);

        if (!worker.hasHost() || !worker.hasPort()) {
            throw new HttpException("Attributes: `host` and `port` are required.", 400);
        }

        workerRepository.add(worker);
        String serializedWorker = this.gson.toJson(worker);

        http.getResponseHeaders().add("Content-Type", "application/json");
        http.sendResponseHeaders(200, serializedWorker.getBytes().length);
        OutputStream os = http.getResponseBody();
        os.write(serializedWorker.getBytes());
    }
}
