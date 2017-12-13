package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import repository.LogRepository;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LogInputHandler implements HttpHandler {

    private HttpHandler defaultHandler;
    private LogRepository logRepository;

    public LogInputHandler(HttpHandler defaultHandler, LogRepository logRepository) {
        this.defaultHandler = defaultHandler;
        this.logRepository = logRepository;
    }

    @Override
    public void handle(HttpExchange http) throws IOException {
        if (!http.getRequestURI().getPath().equals("/logs")) {
            defaultHandler.handle(http);
            return;
        }

        switch (http.getRequestMethod()) {
            case "POST":
                this.saveLog(http);
                break;
            case "GET":
                this.getLogs(http);
                break;
            default:
                this.error("Method " + http.getRequestMethod() + " is not supported.", 405, http);
                break;
        }

        http.close();
    }

    private void error(String message, int statusCode, HttpExchange http) throws IOException {
        String error = "{\"error\": \"" + message + "\"}";
        http.getResponseHeaders().add("Content-Type", "application/json");
        http.sendResponseHeaders(statusCode, error.getBytes().length);
        OutputStream os = http.getResponseBody();
        os.write(error.getBytes());
    }

    private void getLogs(HttpExchange http) throws IOException {
        Map<String, String> qs = parseQueryString(http.getRequestURI().getQuery());

        int offset = 0;
        if (qs.containsKey("offset")) {
            String offsetValue = qs.get("offset");
            try {
                int offsetInt = Integer.parseInt(offsetValue);
                if (offsetInt < 0) {
                    this.error("Offset must be an unsigned int.", 404, http);
                    return;
                }
                offset = offsetInt;
            } catch (NumberFormatException ignored) {
            }
        }

        int limit = 10;
        if (qs.containsKey("limit")) {
            String limitValue = qs.get("limit");
            try {
                int limitInt = Integer.parseInt(limitValue);
                if (limitInt < 0) {
                    this.error("Limit must be an unsigned int.", 404, http);
                    return;
                }
                limit = limitInt;
            } catch (NumberFormatException ignored) {
            }
        }

        Collection<Map<String, String>> logs = logRepository.getLogs(offset, limit);
        String serializedLogs = this.serializeCollection(logs);

        http.getResponseHeaders().add("Content-Type", "application/json");
        http.sendResponseHeaders(200, serializedLogs.getBytes().length);
        OutputStream os = http.getResponseBody();
        os.write(serializedLogs.getBytes());
    }

    private String serializeObject(Map<String, String> object) {
        if (object.isEmpty()) {
            return "{}";
        }

        StringBuilder objectBuilder = new StringBuilder();
        objectBuilder.append("{");
        Set<Map.Entry<String,String>> attributesSet = object.entrySet();
        for (Map.Entry<String, String> entry : attributesSet) {
            String str = "\"" + entry.getKey().replace("\"", "\\\"") + "\":\"" + entry.getValue().replace("\"", "\\\"") + "\",";
            objectBuilder.append(str);
        }
        if(attributesSet.size() != 1) {
            objectBuilder.setLength(objectBuilder.length() - 1);
        }
        objectBuilder.append("}");
        return objectBuilder.toString();
    }

    private String serializeCollection(Collection<Map<String, String>> collection) {
        if (collection.isEmpty()) {
            return "[]";
        }

        StringBuilder collectionBuilder = new StringBuilder();
        collectionBuilder.append("[");
        for (Map<String, String> object : collection) {
            collectionBuilder.append(this.serializeObject(object) + ",");
        }
        if(collection.size() != 1) {
            collectionBuilder.setLength(collectionBuilder.length() - 1);
        }
        collectionBuilder.append("]");

        return collectionBuilder.toString();
    }

    private void saveLog(HttpExchange http) throws IOException {

        StringBuilder bodyBuilder = new StringBuilder();

        try (Reader reader = new BufferedReader(new InputStreamReader(http.getRequestBody(), Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c;
            while ((c = reader.read()) != -1) {
                bodyBuilder.append((char) c);
            }
        }

        logRepository.saveLog(bodyBuilder.toString());
        http.sendResponseHeaders(201, 0);
        System.out.println("Log saved successfully");
    }

    public static Map<String, String> parseQueryString(String qs) {
        Map<String, String> result = new HashMap<>();
        if (qs == null) {
            return result;
        }

        int last = 0, next, l = qs.length();
        while (last < l) {
            next = qs.indexOf('&', last);
            if (next == -1)
                next = l;

            if (next > last) {
                int eqPos = qs.indexOf('=', last);
                try {
                    if (eqPos < 0 || eqPos > next) {
                        result.put(URLDecoder.decode(qs.substring(last, next), "utf-8"), "");
                    } else {
                        result.put(URLDecoder.decode(qs.substring(last, eqPos), "utf-8"), URLDecoder.decode(qs.substring(eqPos + 1, next), "utf-8"));
                    }
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
            last = next + 1;
        }
        return result;
    }
}
