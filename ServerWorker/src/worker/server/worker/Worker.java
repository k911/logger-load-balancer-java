package worker.server.worker;

import worker.communication.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.util.logging.Logger;

public class Worker implements Runnable {

    private final static Logger logger = Logger.getLogger(Worker.class.getName());

    private Connection connection;
    private Socket client;
    private String clientName;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private boolean shouldRead = true;
    private String workerName;
    private Long currentQuizId;

    public Worker(Socket client) {
        this.client = client;
        this.workerName = "server " + Thread.currentThread().getName();
        this.connection = connection;
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
                output.writeObject(new OkResponseMessage(clientName));
                System.out.println("Client logged on:" + clientName);
            } else
                output.writeObject(new EndCommunicationMessage(workerName,"End of communication"));


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
                        //#TODO implement this
                        if (receivedMessage instanceof RequestQuizListMessage) {

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
