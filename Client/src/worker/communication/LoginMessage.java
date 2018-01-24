package worker.communication;


import java.io.Serializable;

public class LoginMessage extends SocketMessage implements Serializable {

    private static final long serialVersionUID = -6388116291340349359L;

    public LoginMessage(String author) {
        super(author);
    }

    @Deprecated
    public LoginMessage() {
    }
}
