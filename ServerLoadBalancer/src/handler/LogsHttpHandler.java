package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import items.HttpException;
import items.Log;
import repository.LogRepository;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class LogsHttpHandler implements HttpHandler {

    private HttpExceptionHandler exceptionHandler;
    private DefaultHttpHandler defaultHandler;
    private LogRepository logRepository;
    private Gson gson;

    public LogsHttpHandler(HttpExceptionHandler exceptionHandler, DefaultHttpHandler defaultHandler, LogRepository logRepository, Gson gson) {
        this.exceptionHandler = exceptionHandler;
        this.defaultHandler = defaultHandler;
        this.logRepository = logRepository;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange http) throws IOException {
        if (!http.getRequestURI().getPath().equals("/logs")) {
            this.defaultHandler.handle(http);
        }

        try {
            switch (http.getRequestMethod()) {
                case "POST":
                    this.saveLog(http);
                    break;
                case "GET":
                    this.getLogs(http);
                    break;
                default:
                    throw new HttpException("Method " + http.getRequestMethod() + " is not supported.", 405);
            }
        } catch (HttpException e) {
            this.exceptionHandler.handle(e, http);
        }

        http.close();
    }

    private void getLogs(HttpExchange http) throws IOException, HttpException {
        Map<String, String> qs = this.defaultHandler.parseQueryString(http.getRequestURI().getQuery());

        HttpException exception = new HttpException("Bad request", 400);
        int offset = 0;
        if (qs.containsKey("offset")) {
            String offsetValue = qs.get("offset");
            try {
                int offsetInt = Integer.parseInt(offsetValue);
                if (offsetInt < 0) {
                    exception.addError("Offset must be an unsigned int.");
                }
                offset = offsetInt;
            } catch (NumberFormatException e) {
                exception.addError(e.getMessage());
            }
        }

        int limit = 10;
        if (qs.containsKey("limit")) {
            String limitValue = qs.get("limit");
            try {
                int limitInt = Integer.parseInt(limitValue);
                if (limitInt < 0) {
                    exception.addError("Limit must be an unsigned int.");
                }
                limit = limitInt;
            } catch (NumberFormatException e) {
                exception.addError(e.getMessage());
            }
        }

        if(exception.hasErrors()) {
            throw exception;
        }

        Collection<Log> logs = logRepository.findAll(offset, limit);
        String serializedLogs = this.gson.toJson(logs);

        http.getResponseHeaders().add("Content-Type", "application/json");
        http.sendResponseHeaders(200, serializedLogs.getBytes().length);
        OutputStream os = http.getResponseBody();
        os.write(serializedLogs.getBytes());
    }

    private void saveLog(HttpExchange http) throws IOException, HttpException {

        StringBuilder bodyBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader(http.getRequestBody(), Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c;
            while ((c = reader.read()) != -1) {
                bodyBuilder.append((char) c);
            }
        }

        Log log = gson.fromJson(bodyBuilder.toString(), Log.class);

        if (!log.hasContext() || !log.hasMessage()) {
            throw new HttpException("Attributes: `message` and `context` are required.", 400);
        }

        logRepository.add(log);
        String serializedLog = this.gson.toJson(log);

        http.getResponseHeaders().add("Content-Type", "application/json");
        http.sendResponseHeaders(200, serializedLog.getBytes().length);
        OutputStream os = http.getResponseBody();
        os.write(serializedLog.getBytes());
    }
}
