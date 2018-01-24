package worker.server.worker;

import worker.communication.*;
import worker.communication.job.Job;
import worker.server.ThreadSafeSet;
import worker.server.config.WorkerConfiguration;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class RunnableWorker implements Runnable {

    private final static Logger logger = Logger.getLogger(RunnableWorker.class.getName());

    private Socket client;
    private String clientName;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private boolean shouldRead = true;
    private ThreadSafeSet<String> connectedUsers;
    ExecutorService executors;
    private String workerName;
    int threadPoolSize;
    TimeUnit timeUnit;
    int shutdownTimeOut;


    public RunnableWorker(Socket client) {

    }

    public RunnableWorker(Socket client, WorkerConfiguration workerConfiguration, ThreadSafeSet<String> connectedUsers) {
        this.client = client;
        this.connectedUsers=connectedUsers;
        this.configure(workerConfiguration);
        executors = Executors.newFixedThreadPool(this.threadPoolSize);
    }

    private void configure(WorkerConfiguration workerConfiguration) {

        this.workerName = workerConfiguration.getName().orElse("RunnableWorker") +"" + Thread.currentThread().getName();
        this.threadPoolSize=workerConfiguration.getThreadPoolSize().orElse(4);
        this.shutdownTimeOut=workerConfiguration.getExecutorShutdownTimeout().orElse(10);
        this.timeUnit=workerConfiguration.getTimeoutUnit().orElse(TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        logger.info("RunnableWorker: " + Thread.currentThread().getName() + " is started");
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
                        JobRequest message=(JobRequest) receivedMessage;

                        if(validateJobs(message.getJobs())){





                        }else
                            sendRejectionMessage("Received jobs were not prepared properly");



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

    private boolean validateJobs(Map<Long, Job> jobs) {
        boolean isValid=true;
        for (Job job : jobs.values()) {
            if (job.getArgument()==null || job.getArgument().isEmpty() || job.getJobType()==null)
            {
                isValid=false;
                break;
            }
        }
        return isValid;
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
