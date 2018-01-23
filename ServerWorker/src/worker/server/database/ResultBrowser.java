package ServerWorker.src.worker.server.database;

import ServerWorker.src.worker.server.database.DaoBase;
import ServerWorker.src.worker.communication.job.Question;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ResultBrowser extends DaoBase implements IResultBrowser {
    private final static Logger logger = Logger.getLogger(ResultBrowser.class.getName());
    private String usedSchema;
    private Statement st;
    private IQuizDAO quizDAO;

    public ResultBrowser(Connection connection, String usedSchema) {
        setDbConnection(connection);
        quizDAO = new QuizDAO(connection, usedSchema);
        st = createStatement(connection);
    }

    @Override
    public List<Long> getQuizIds() {
        return quizDAO.getQuizList();

    }

    @Override
    public Map<Long, List<Long>> getQuizQuestionIds(List<Long> quizIds) {

        Map<Long, List<Long>> quizQuestionsMap = new HashMap<>();

        for (Long id : quizIds) {
            Optional<List<Question>> result = quizDAO.getQuiz(id);
            if (result.isPresent()) {
                List<Long> questionIds = result.get().stream().map(Question::getId).collect(Collectors.toList());
                quizQuestionsMap.put(id, questionIds);
            }
        }

        return quizQuestionsMap;
    }

    @Override
    public Map<Long, List<Long>> getAnswersCounts(Long quizId) {
        System.out.println("getUserAnswers");

        String condition = "WHERE " +
                "results.quiz_id=" + quizId.toString();

        String sql = "SELECT COUNT(*),quiz_id,question_id,answer_id " +
                "FROM `results` " +
                condition +
                " Group by quiz_id,question_id,answer_id " +
                " ORDER BY quiz_id,question_id,answer_id ASC;";

        ResultSet results = executeQuery(st, sql);
        Map<Long, List<Long>> questionAnswers = new HashMap<>();

        try {
            while (results.next()) {
                Long count = results.getLong(1);

                Long questionId = results.getLong("question_id");
                if (!questionAnswers.containsKey(questionId)) {
                    List<Long> answers = new ArrayList<>();
                    questionAnswers.put(questionId, answers);
                }
                questionAnswers.get(questionId).add(count);
            }
        } catch (SQLException e) {
            logger.warning("ResultBrowser: Query failed:" + e.getMessage());
            return null;
        }
        return questionAnswers;
    }

    @Override
    protected boolean initialize() {

        this.st = createStatement(this.getDbConnection());

        return selectSchema(this.usedSchema, st);
    }


    private ResultSet executeQuery(Statement s, String sql) {
        try {
            return s.executeQuery(sql);
        } catch (SQLException e) {
            logger.warning(
                    "Query ws not executed! " + e.getMessage() + " Error Code: " + e.getErrorCode());
        }
        return null;
    }
}
