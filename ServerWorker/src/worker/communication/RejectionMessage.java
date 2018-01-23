package worker.communication;


import java.io.Serializable;


public class RejectionMessage extends SocketMessage implements Serializable {

    private static final long serialVersionUID = -5364146378849441305L;
    private Long quizId;
    private String reason;

    public RejectionMessage(String author, Long quizId, String reason) {
        super(author);
        this.quizId = quizId;
        this.reason = reason;

    }

    @Deprecated
    public RejectionMessage() {

    }

    public String getReason() {
        return reason;
    }


}
