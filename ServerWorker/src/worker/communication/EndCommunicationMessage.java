package worker.communication;

import java.io.Serializable;

public class EndCommunicationMessage extends SocketMessage implements Serializable {

    private static final long serialVersionUID = -5751621698248274612L;
    private final String message;

    public EndCommunicationMessage(String author) {
        super(author);
        message = null;
    }

    public EndCommunicationMessage(String author, String message) {
        super(author);
        this.message = message;
    }

    @Deprecated
    public EndCommunicationMessage() {
        message = null;
    }
}
