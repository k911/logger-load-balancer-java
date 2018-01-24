package worker.communication.scheduler;


import java.io.Serializable;
import java.sql.Timestamp;

public class Log implements Serializable {

    private static final long serialVersionUID = 7924725717075936634L;

    private int id = -1;
    private String message = null;
    private String context = null;
    private Timestamp createdAt;


    public Log() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public Log(String message, String context) {
        this.message = message;
        this.context = context;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public Log(String message, String context, Timestamp createdAt) {
        this.message = message;
        this.context = context;
        this.createdAt = createdAt;
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
