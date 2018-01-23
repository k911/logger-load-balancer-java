package ServerWorker.src.worker.communication.job;

import java.io.Serializable;
import java.util.List;

public class Answer implements Serializable {
    private static final long serialVersionUID = 2631924532682336018L;
    private Long questionId;
    private List<Long> answerId;


    public Answer(Long questionId, List<Long> answerId) {
        this.questionId = questionId;
        this.answerId = answerId;
    }


    @Deprecated
    public Answer() {

    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public List<Long> getAnswerId() {
        return answerId;
    }

    public void setAnswerId(List<Long> answerId) {
        this.answerId = answerId;
    }

}
