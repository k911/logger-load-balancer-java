package worker.communication;


import java.io.Serializable;


public class OkResponseMessage extends SocketMessage implements Serializable {


    private static final long serialVersionUID = -7288386688322415847L;

    public OkResponseMessage(String author) {
        super(author);

    }

    @Deprecated
    public OkResponseMessage() {

    }
}
