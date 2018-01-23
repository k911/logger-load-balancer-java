package worker.communication;

import java.io.Serializable;
import java.util.List;

public class QuizListMessage extends SocketMessage implements Serializable {

    private static final long serialVersionUID = 3590476803891320170L;
    private List<Long> quizes;
    //private Long quizId;

    public QuizListMessage(String author) {
        super(author);
    }

    @Deprecated
    public QuizListMessage() {

    }

    public List<Long> getQuizes() {
        return quizes;
    }

    public void setQuizes(List<Long> quizes) {
        this.quizes = quizes;
    }

}
