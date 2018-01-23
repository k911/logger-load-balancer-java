package ServerWorker.src.worker.communication;


import ServerWorker.src.worker.communication.job.Answer;

import java.io.Serializable;
import java.util.List;


public class QuizAnswerMessage extends SocketMessage implements Serializable {


    private static final long serialVersionUID = 8100311873209486609L;
    private List<Answer> answers;
    private Long quizId;

    public QuizAnswerMessage(String author) {
        super(author);

    }

    @Deprecated
    public QuizAnswerMessage() {

    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }
}
