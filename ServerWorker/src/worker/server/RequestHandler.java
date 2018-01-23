package ServerWorker.src.worker.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

public class RequestHandler implements HttpHandler {

    ErrorHandler errorHandler;
    ExecutorService executors;

    public RequestHandler(ErrorHandler errorHandler, ExecutorService executors) {
        this.errorHandler = errorHandler;
        this.executors = executors;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        System.out.println("Handle for " + httpExchange.getRequestURI());


        if (!httpExchange.getRequestURI().getPath().startsWith("/request")) {
            errorHandler.handle(httpExchange);
            return;
        }

        switch (httpExchange.getRequestMethod()) {
            case "POST":
                //#TODO schedule jobs and return 202:Accepted and return Location: repsonse/{request id} ?
                break;
            default:
                //#TODO -return some error
                break;
        }


    }
}
