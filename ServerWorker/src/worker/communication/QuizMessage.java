package ServerWorker.src.worker.communication;


import ServerWorker.src.worker.communication.job.Question;

import java.io.Serializable;
import java.util.List;


public class QuizMessage extends SocketMessage implements Serializable {


    private static final long serialVersionUID = -7288386688322415847L;
    private List<Question> questions;
    private Long quizId;

    public QuizMessage(String author) {
        super(author);

    }

    @Deprecated
    public QuizMessage() {

    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
