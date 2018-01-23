package worker.server.database;

import java.util.List;
import java.util.Map;

public interface IResultBrowser {

    List<Long> getQuizIds();

    Map<Long, List<Long>> getQuizQuestionIds(List<Long> quizIds);

    Map<Long, List<Long>> getAnswersCounts(Long quizId);


}
