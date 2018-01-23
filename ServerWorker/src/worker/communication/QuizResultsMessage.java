package ServerWorker.src.worker.communication;


import ServerWorker.src.worker.communication.job.Result;

import java.io.Serializable;
import java.util.List;


public class QuizResultsMessage extends SocketMessage implements Serializable {


    private static final long serialVersionUID = 2380171915101252891L;
    private List<Result> results;
    private Float score;


    public QuizResultsMessage(String author, List<Result> results) {
        super(author);
        this.results = results;
        calculateScore();
    }

    public QuizResultsMessage(String author) {
        super(author);
        this.score = 0f;
    }

    @Deprecated
    public QuizResultsMessage() {

    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
        calculateScore();
    }

    private void calculateScore() {
        int size = results.size();
        int validCount = 0;
        for (Result result : results) {
            if (result.isSuccessful())
                ++validCount;
        }

        this.score = Float.valueOf((float) validCount / size);
    }

    public Float getScore() {
        return score;
    }
}
