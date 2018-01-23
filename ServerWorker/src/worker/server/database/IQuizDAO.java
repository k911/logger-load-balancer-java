package ServerWorker.src.worker.server.database;

import ServerWorker.src.worker.communication.job.Question;
import ServerWorker.src.worker.communication.job.Result;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IQuizDAO {


    public List<Long> getQuizList();

    public Optional<List<Question>> getQuiz(Long quizId);

    public Optional<List<Result>> getCorrectAnswers(Long quizId);

    public boolean persistAnswer(String user, Long quizId, Long questionId, Long answerId);

    public Optional<Map<Long, Long>> getUserScores(String user);

    public Optional<List<Result>> getUserAnswers(String user, Long quizId);

}
