package worker.communication;


import java.io.Serializable;

public class RequestQuizListMessage extends SocketMessage implements Serializable {

    private static final long serialVersionUID = -6532683589417248936L;

    public RequestQuizListMessage(String requesterName) {
        super(requesterName);
    }

    @Deprecated
    public RequestQuizListMessage() {
    }


}
