package ServerWorker.src.worker.communication.job;

import java.io.Serializable;
import java.util.Map;


public class Question implements Serializable {


    private static final long serialVersionUID = 2343482157709020208L;
    private QuestionType questionType;
    private Long id;
    private String question;
    private Map<Long, String> possibleAnswers;

    public Question(Long id, String question, Map<Long, String> possibleAnswers) {
        this.id = id;
        this.question = question;
        this.possibleAnswers = possibleAnswers;
    }

    public Question() {
        this.questionType = QuestionType.OneOf;
    }

    @Override
    public String toString() {
        return "Question{" +
                "questionType=" + questionType +
                ", id=" + id +
                ", question='" + question + '\'' +
                ", possibleAnswers=" + possibleAnswers +
                '}';
    }


    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Map<Long, String> getPossibleAnswers() {
        return possibleAnswers;
    }

    public void setPossibleAnswers(Map<Long, String> possibleAnswers) {
        this.possibleAnswers = possibleAnswers;
    }


}
