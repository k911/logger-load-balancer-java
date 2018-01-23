package worker.server.database;

import worker.communication.job.Question;
import worker.communication.job.QuestionType;
import worker.communication.job.Result;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class QuizDAO implements IQuizDAO {
    private final static Logger logger = Logger.getLogger(QuizDAO.class.getName());
    private Connection connection;
    private Statement st;

    public QuizDAO(Connection connection, String usedSchema) {
        this.connection = connection;
        st = createStatement(this.connection);
        selectSchema(usedSchema, st);

    }

    protected boolean selectSchema(String usedSchema, Statement st) {
        System.out.println("selectSchema");
        return executeUpdate(st, "USE " + usedSchema + ";") == 0;
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

    @Override
    public List<Long> getQuizList() {
        System.out.println("getQuizList");
        String[] columns = new String[]{"id"};
        String[] from = {"quiz"};
        String condition = "active IS TRUE";

        String sql = new QueryBuilder()
                .select(Arrays.asList(columns))
                .from(Arrays.asList(from))
                .where(condition)
                .BuildQuery();
        ResultSet results = executeQuery(st, sql);

        List<Long> quizIds = new ArrayList<>();
        try {
            while (results.next())
                quizIds.add(results.getLong("id"));
        } catch (SQLException e) {
            logger.warning("Query failed:" + e.getMessage());
            return null;
        }

        return quizIds;

    }

    @Override
    public Optional<List<Question>> getQuiz(Long quizId) {
        System.out.println("getQuiz");
        List<String> columns = Arrays
                .asList("question.quiz_id,question.id,question.text,answers.id as answerId,answers.text as answerText");
        List<String> from = Arrays.asList("question");
        String condition = "question.quiz_id=" + quizId;
        String joinCondition = "question.id=answers.question_id";

        String sql = "SELECT question.id,question.text,answers.id as AnswerId,answers.text as AnswerText " +
                "from question " +
                "left join answers on " +
                "question.quiz_id=answers.quiz_id\n" +
                "AND question.id=answers.question_id\n" +
                "where question.quiz_id=" + quizId + ";";

        ResultSet results = executeQuery(st, sql);


        Map<Long, Question> questionMap = new HashMap<>();

        try {

            if (!results.isBeforeFirst())
                return null;

            while (results.next()) {

                Long questionId = results.getLong("question.id");
                if (!questionMap.containsKey(questionId)) {
                    Question question = new Question();
                    question.setId(questionId);
                    question.setQuestion(results.getString("question.text"));
                    question.setPossibleAnswers(new TreeMap<>());
                    question.setQuestionType(QuestionType.OneOf);
                    questionMap.put(questionId, question);
                }
                Map<Long, String> possibleAnswers = questionMap.get(questionId).getPossibleAnswers();
                possibleAnswers.put(results.getLong("answerId"),
                        results.getString("answerText"));

                if (possibleAnswers.size() > 1)
                    questionMap.get(questionId).setQuestionType(QuestionType.Multiple);
            }

        } catch (SQLException e) {
            logger.warning("Query failed:" + e.getMessage());
            return Optional.empty();
        }

        List<Question> questions = questionMap.values().stream().collect(Collectors.toList());
        return Optional.ofNullable(questions);

    }

    @Override
    public Optional<List<Result>> getCorrectAnswers(Long quizId) {
        System.out.println("getCorrectAnswers");
        Optional<List<Question>> quiz = this.getQuiz(quizId);

        if (!quiz.isPresent())
            return Optional.empty();

        List<Long> questionListId = new ArrayList<>();

        for (Question question : quiz.get())
            questionListId.add(question.getId());


        List<String> columns = Arrays.asList("correct_answers.question_id,correct_answers.answer_id");
        List<String> from = Arrays.asList("correct_answers");
        String condition = "quiz_id=" + quizId;


        String sql = new QueryBuilder()
                .select(columns)
                .from(from)
                .where(condition)
                .BuildQuery();

        ResultSet results = executeQuery(st, sql);

        Map<Long, Result> questionResult = new HashMap<>();

        try {
            while (results.next()) {

                Long questionId = results.getLong("correct_answers.question_id");
                if (!questionResult.containsKey(questionId)) {
                    Result result = new Result();
                    result.setQuestionId(questionId);
                    result.setProvidedAnswer(new ArrayList<>());
                    result.setValidAnswers(new ArrayList<>());

                    questionResult.put(questionId, result);
                }
                Long validAnswer = results.getLong("correct_answers.answer_id");
                if (!questionResult.get(questionId).getValidAnswers().contains(validAnswer))
                    questionResult.get(questionId).getValidAnswers().add(validAnswer);

            }

        } catch (SQLException e) {
            logger.warning("Query failed:" + e.getMessage());
            return null;
        }
        List<Result> answers = questionResult.values().stream().collect(Collectors.toList());
        return Optional.ofNullable(answers);

    }

    @Override
    public boolean persistAnswer(String user, Long quizId, Long questionId, Long answerId) {
        System.out.println("persistAnswer");
        String tableName = "results";
        List<String> answerColumns = Arrays.asList("quiz_id", "question_id", "NIU", "answer_id");
        List<String> answerValues = Arrays
                .asList("\'" + quizId.toString() + "\'", "\'" + questionId.toString() + "\'", "\'" + user + "\'",
                        "\'" + answerId.toString() + "\'");

        String sql = new QueryBuilder()
                .insert()
                .table(tableName)
                .values(answerColumns, answerValues)
                .BuildQuery();

        if (executeUpdate(st, sql) == 1)
            return true;
        else
            return false;

    }

    @Override
    public Optional<Map<Long, Long>> getUserScores(String userId) {
        System.out.println("getUserScores");
        Map<Long, Long> userScores = new HashMap<>();
        List<Long> quizList = this.getQuizList();

        if (quizList.isEmpty())
            return null;

        for (Long id : quizList) {
            Optional<List<Result>> answers = getUserAnswers(userId, id);
            if (answers.isPresent()) {
                Long score = 0l;
                Long maxScore = 0l;
                for (Result result : answers.get()) {
                    ++maxScore;
                    if (result.isSuccessful()) ;
                    ++score;
                }
                Long finalScore = score == maxScore ? maxScore : ((long) ((float) score / maxScore * 100));

                userScores.put(id, finalScore);

            }
        }

        return Optional.ofNullable(userScores);


    }

    @Override
    public Optional<List<Result>> getUserAnswers(String user, Long quizId) {
        System.out.println("getUserAnswers");
        Optional<List<Result>> quizResults = getCorrectAnswers(quizId);
        if (!quizResults.isPresent())
            return null;

        Set<Long> questionListId = new HashSet<>();
        for (Result result : quizResults.get())
            questionListId.add(result.getQuestionId());

        List<String> columns = Arrays.asList("question_id,answer_id");
        List<String> from = Arrays.asList("results");
        String condition = " NIU=" + '\'' + user + '\'' + " AND quiz_id= " + quizId.toString();

        String sql = new QueryBuilder()
                .select(columns)
                .from(from)
                .where(condition)
                .BuildQuery();

        ResultSet results = executeQuery(st, sql);
        Map<Long, List<Long>> questionAnswers = new HashMap<>();
        try {
            if (results == null)
                return Optional.empty();
            else {
                if (!results.isBeforeFirst()) {
                    return Optional.empty();
                }
            }


            System.out.println("Before while");
            while (results.next()) {

                Long questionId = results.getLong("question_id");
                if (!questionAnswers.containsKey(questionId)) {
                    List<Long> answers = new ArrayList<>();
                    questionAnswers.put(questionId, answers);
                }
                Long userAnswer = results.getLong("answer_id");
                if (!questionAnswers.get(questionId).contains(userAnswer))
                    questionAnswers.get(questionId).add(userAnswer);

            }

            for (Result result : quizResults.get()) {
                result.setProvidedAnswer(questionAnswers.get(result.getQuestionId()));
            }
        } catch (SQLException e) {
            logger.warning("Query failed:" + e.getMessage());
            return null;
        }


        return quizResults;
    }

    private int executeUpdate(Statement s, String sql) {
        try {
            return s.executeUpdate(sql);
        } catch (SQLException e) {
            logger.warning("sql statement: " + sql + " could not be executed " + e.getMessage() + " Error Code: " +
                    e.getErrorCode());
            return -1;
        }

    }

    private Statement createStatement(Connection connection) {
        try {
            return connection.createStatement();
        } catch (SQLException e) {
            logger.warning(
                    "Exception thrown when creating Statement " + e.getMessage() + " Error Code: " + e.getErrorCode());
            return null;
        }

    }

}
