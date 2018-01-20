package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import items.HttpException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class DefaultHttpHandler implements HttpHandler {

    private HttpExceptionHandler exceptionHandler;

    public DefaultHttpHandler(HttpExceptionHandler exceptionHandler) {

        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public void handle(HttpExchange http) throws IOException {
        String error = "{\"error\": \"No route for " + http.getRequestMethod() + " " + http.getRequestURI() + ".\"}";
        this.exceptionHandler.handle(new HttpException(error, 404), http);
        http.close();
    }

    public Map<String, String> parseQueryString(String qs) {
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
