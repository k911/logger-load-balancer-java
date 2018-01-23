package ServerWorker.src.worker.communication;


import java.io.Serializable;
import java.time.LocalDateTime;

public abstract class SocketMessage implements Serializable {

    private static final long serialVersionUID = 8348072401988203243L;
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final String author;

    @Deprecated
    SocketMessage() {

        author = null;
    }


    SocketMessage(String author) {
        this.author = author;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getAuthor() {
        return author;
    }


}
