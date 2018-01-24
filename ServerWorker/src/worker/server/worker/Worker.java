package worker.server.worker;

import worker.communication.*;
import worker.server.ThreadSafeSet;
import worker.server.config.WorkerConfiguration;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Worker implements Runnable {

    private final static Logger logger = Logger.getLogger(Worker.class.getName());

    private Socket client;
    private String clientName;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private boolean shouldRead = true;

    private String workerName;
    int threadPoolSize;
    TimeUnit timeUnit;
    int shutdownTimeOut;


    public Worker(Socket client) {

    }

    public Worker(Socket client, WorkerConfiguration workerConfiguration, ThreadSafeSet<String> connectedUsers) {
        this.client = client;
        this.configure(workerConfiguration);

    }

    private void configure(WorkerConfiguration workerConfiguration) {

        this.workerName = workerConfiguration.getName().orElse("Worker") +"" + Thread.currentThread().getName();
        this.threadPoolSize=workerConfiguration.getThreadPoolSize().orElse(4);
        this.shutdownTimeOut=workerConfiguration.getExecutorShutdownTimeout().orElse(10);
        this.timeUnit=workerConfiguration.getTimeoutUnit().orElse(TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        logger.info("Worker: " + Thread.currentThread().getName() + " is started");
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
                output.writeObject(new OkResponseMessage(workerName));
                logger.info("Client logged on:" + clientName);
            } else {
                output.writeObject(new EndCommunicationMessage(workerName, "End of communication"));
                performClose();
            }
            while (!client.isClosed()) {
                System.out.println("Waiting for message");
                Object receivedMessage = input.readObject();
                if (!isValidMessage(receivedMessage)) {
                    sendRejectionMessage("Unrecognized message received!");
                } else {
                    String author = ((SocketMessage) receivedMessage).getAuthor();
                    logger.info("Parsing message:" + receivedMessage.getClass() +" from "+author);
                    if (!author.equalsIgnoreCase(clientName)) {
                        EndCommunicationMessage endMessage = new EndCommunicationMessage(workerName,
                                "Identity changed!");
                        output.writeObject(endMessage);
                        performClose();
                        break;
                    }

                    if (receivedMessage instanceof JobRequest) {
                        //#TODO implement logic here - perform all jobs, and send response then close all connections?



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
        return (isValidMessage(message) && message instanceof LoginMessage);
    }

    private void sendRejectionMessage(String message) throws IOException {
        output.writeObject(new RejectionMessage(workerName, null, message));
    }


}
