package ServerWorker.src.worker.communication;


import java.io.Serializable;


public class ResultsRequestMessage extends SocketMessage implements Serializable {


    private static final long serialVersionUID = -7288386688322415847L;
    private Long quizId;

    public ResultsRequestMessage(String author) {
        super(author);

    }

    @Deprecated
    public ResultsRequestMessage() {

    }

    public ResultsRequestMessage(String author, Long quizId) {
        super(author);
        this.quizId = quizId;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }
}
