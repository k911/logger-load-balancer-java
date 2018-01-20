package items;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.sql.Timestamp;

public class Log implements Serializable {

    @Expose
    private int id = -1;

    @Expose
    private String message = null;

    @Expose
    private String context = null;

    @Expose
    private Timestamp createdAt;

    public Log() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public Log(int id, String message, String context, Timestamp createdAt) {
        this.setId(id);
        this.context = context;
        this.message = message;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getContext() {
        return context;
    }

    public String getMessage() {
        return message;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new RuntimeException("Log id must be an unsigned integer.");
        }

        this.id = id;
    }

    public boolean hasContext() {
        return context != null && !context.isEmpty();
    }

    public boolean hasMessage() {
        return message != null && !message.isEmpty();
    }
}
