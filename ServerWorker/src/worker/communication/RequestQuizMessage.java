package worker.communication;

import java.io.Serializable;

public class RequestQuizMessage extends SocketMessage implements Serializable {


    private static final long serialVersionUID = -3909434982269978664L;
    private Long quizId;

    public RequestQuizMessage(Long quizId, String requesterName) {
        super(requesterName);
        this.quizId = quizId;

    }

    @Deprecated
    public RequestQuizMessage() {
    }


    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }
}
