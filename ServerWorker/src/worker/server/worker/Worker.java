package worker.server.worker;

import worker.communication.job.Answer;
import worker.communication.job.Question;
import worker.communication.job.Result;
import worker.server.ThreadSafeSet;
import worker.server.database.IQuizDAO;
import worker.server.database.QuizDAO;
import worker.communication.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class Worker implements Runnable {

    private final static Logger logger = Logger.getLogger(Worker.class.getName());
    private ThreadSafeSet<String> connectedUsers;
    private Connection connection;
    private Socket client;
    private String clientName;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private boolean shouldRead = true;
    private String workerName;
    private IQuizDAO quizDAO;
    private Long currentQuizId;

    public Worker(Socket client, ThreadSafeSet<String> connectedUsers, Connection connection, String usedSchema) {
        this.client = client;
        this.workerName = "server " + Thread.currentThread().getName();
        this.connectedUsers = connectedUsers;
        this.connection = connection;
        quizDAO = new QuizDAO(connection, usedSchema);
        currentQuizId = null;
    }

    @Override
    public void run() {
        logger.info("Worker: " + Thread.currentThread().getName() + " is started");
        //do stuff

        try {
            output = new ObjectOutputStream(client.getOutputStream());
            input = new ObjectInputStream(client.getInputStream());
        } catch (IOException e) {
            logger.warning(e.getMessage());
            performClose();
            return;
        }
        try {
            System.out.println("Waiting for first message");
            Object firstMessage = input.readObject();
            if (isLoginMessage(firstMessage)) {
                clientName = ((SocketMessage) firstMessage).getAuthor();
                if (!connectedUsers.set(clientName)) {
                    output.writeObject(new EndCommunicationMessage(workerName, "Client already logged in"));
                    performClose();
                } else {
                    output.writeObject(new OkResponseMessage(clientName));
                    System.out.println("Client logged on:" + clientName);
                }
            }

            while (!client.isClosed()) {
                System.out.println("Waiting for message");
                Object receivedMessage = input.readObject();

                if (!isValidMessage(receivedMessage)) {
                    sendRejectionMessage("Unrecognized message received!");
                } else {
                    logger.info("Parsing valid message:" + receivedMessage.getClass());

                    String author = ((SocketMessage) receivedMessage).getAuthor();
                    if (!author.equalsIgnoreCase(clientName)) {
                        EndCommunicationMessage endMessage = new EndCommunicationMessage(workerName,
                                "Identity changed!");
                        output.writeObject(endMessage);
                        performClose();
                        break;
                    }

                    if (receivedMessage instanceof RequestQuizListMessage) {
                        QuizListMessage quizList = new QuizListMessage(workerName);
                        quizList.setQuizes(quizDAO.getQuizList());
                        System.out.println("Sending QuizListMessage");
                        output.writeObject(quizList);

                    } else if (receivedMessage instanceof RequestQuizMessage) {
                        RequestQuizMessage request = (RequestQuizMessage) receivedMessage;

                        if (hasUserSolvedQuiz(request.getQuizId(), request.getAuthor())) {
                            sendRejectionMessage("User has already taken quiz");
                        }
                        Optional<List<Question>> quiz = quizDAO.getQuiz(request.getQuizId());
                        if (quiz.isPresent()) {
                            QuizMessage payload = new QuizMessage(workerName);
                            payload.setQuestions(quiz.get());
                            payload.setQuizId(request.getQuizId());
                            System.out.println("Sending QuizMessage to " + clientName);
                            output.writeObject(payload);
                            currentQuizId = request.getQuizId();

                        } else
                            sendRejectionMessage("No quiz found");


                    } else if (receivedMessage instanceof QuizAnswerMessage) {


                        QuizAnswerMessage quizAnswerMessage = (QuizAnswerMessage) receivedMessage;

                        if (!currentQuizId.equals(quizAnswerMessage.getQuizId())) {
                            sendRejectionMessage("Bad quiz id given! Current session quiz id:" + currentQuizId
                                    + " provided " + quizAnswerMessage.getQuizId());
                        } else {
                            List<Long> unpersisted = new ArrayList<>();
                            for (Answer answer : quizAnswerMessage.getAnswers()) {
                                for (Long answerId : answer.getAnswerId()) {
                                    boolean was_persisted = quizDAO
                                            .persistAnswer(clientName, quizAnswerMessage.getQuizId(),
                                                    answer.getQuestionId(), answerId);

                                    if (!was_persisted) {
                                        logger.warning("Could not persist answer user:" + clientName
                                                + " question: " + answer.getQuestionId() +
                                                " answer: " + answerId);
                                        unpersisted.add(answer.getQuestionId());

                                    }
                                }
                            }

                            if (!unpersisted.isEmpty())
                                sendRejectionMessage("Could not persist answers" +
                                        ":" + unpersisted.toString());
                            else {
                                logger.info("Persisted answers from " + clientName);
                                output.writeObject(new OkResponseMessage(clientName));
                            }
                        }


                    } else if (receivedMessage instanceof ResultsRequestMessage) {

                        QuizResultsMessage resultsMessage = new QuizResultsMessage(workerName);
                        Optional<List<Result>> results = quizDAO
                                .getUserAnswers(clientName, ((ResultsRequestMessage) receivedMessage).getQuizId());

                        if (results.isPresent()) {
                            resultsMessage.setResults(results.get());
                            output.writeObject(resultsMessage);
                        } else
                            output.writeObject(new RejectionMessage(workerName, null, "No results found"));

                    } else if (receivedMessage instanceof EndCommunicationMessage) {
                        performClose();
                    } else {
                        sendRejectionMessage("Unhandled message type received");
                    }


                }

            }

        } catch (IOException e) {
            logger.warning(e.getMessage());
            performClose();
            return;
        } catch (ClassNotFoundException e) {
            logger.warning(e.getMessage());
            performClose();
            return;
        }
    }

    private boolean hasUserSolvedQuiz(Long quizId, String author) {

        Optional<List<Result>> results = quizDAO.getUserAnswers(author, quizId);
        boolean hasSolved = results.isPresent() && !results.get().isEmpty();
        System.out.println("hasSolved=" + hasSolved);
        return hasSolved;

    }

    private void performClose() {
        try {
            input.close();
            output.close();
            client.close();
            if (client.isClosed())
                logger.info("Closed connection with client: " + clientName);
        } catch (IOException e) {
            logger.warning(e.getMessage());
        }


    }

    private boolean isValidMessage(Object message) {
        return message instanceof SocketMessage;

    }

    private boolean isLoginMessage(Object message) {
        return (isValidMessage(message) && message instanceof QuizLoginMessage);
    }

    private void sendRejectionMessage(String message) throws IOException {
        output.writeObject(new RejectionMessage(workerName, null, message));
    }


}
