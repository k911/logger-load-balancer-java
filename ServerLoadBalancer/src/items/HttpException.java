package ServerLoadBalancer.src.items;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class HttpException extends Throwable implements Serializable {

    @Expose
    private String message;

    @Expose
    private int statusCode;

    @Expose
    private Collection<String> errors;

    public HttpException(String message, int statusCode) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
        this.errors = new ArrayList<>();
    }

    public HttpException(String message, int statusCode, Collection<String> errors) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
        this.errors = errors;
    }

    public void addError(String error) {
        this.errors.add(error);
    }

    public boolean hasErrors() {
        return !this.errors.isEmpty();
    }

    public int getStatusCode() {
        return statusCode;
    }
}
