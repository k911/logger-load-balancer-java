package worker.server.worker;

import worker.communication.*;
import worker.communication.job.Job;
import worker.communication.scheduler.GetLogsCommand;
import worker.communication.scheduler.Log;
import worker.communication.scheduler.SocketConnectionFactory;
import worker.server.ThreadSafeSet;
import worker.server.config.WorkerConfiguration;
import worker.statistics.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
                        JobRequest message=(JobRequest) receivedMessage;

                        if(validateJobs(message.getJobs())) {
                            Map<Long, Job> jobs = message.getJobs();
                            Long results = null;
                            Future<Boolean> future = null;
                            SocketConnectionFactory socketConnectionFactory = new SocketConnectionFactory("SOCKET_SERVER_HOST", "SOCKET_SERVER_PORT");
                            Job job;
                            ArrayList<Long> logs = null;
                            try {
                                Socket socket = socketConnectionFactory.make();
                                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                                GetLogsCommand getLogsCommand = (GetLogsCommand) in.readObject();
                                // Read logs
                                out.writeUTF("get_logs");
                                out.writeObject(getLogsCommand);
                                out.flush();
                                if (in.readUTF().equals("FAILURE")) {
                                    throw new RuntimeException(in.readUTF());
                                }

                                logs = (ArrayList<Long>) in.readObject();


                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }

                            for (Map.Entry<Long, Job> entry : jobs.entrySet()) {
                                job= entry.getValue();

                                switch (job.getJobType()) {

                                    case CALC_PHRASE_OCCURENCES:
                                        //funkcja dla enum1;
                                        break;

                                    case FIND_MIN:
                                        Min min= new Min();// funkcja dla enum2;
                                        results=min.CalculateMin(logs);
                                        break;

                                    case FIND_MAX:
                                        Max max=new Max();
                                        results=max.CalculateMax(logs);
                                        break;
                                    case CALC_ARITHMETIC_MEAN:
                                        MeanArithmetic mear=new MeanArithmetic();
                                        results=mear.CalculateArithmeticMean(logs);
                                        break;
                                    case CALC_GEOMETRIC_MEAN:
                                       // MeanGeometric mege=new MeanGeometric();
                                        //results=mege.CalculateGeometricMean(logs);
                                        break;
                                    case CALC_MEDIAN:
                                        Median med=new Median();
                                        results=med.CalculateMedian(logs);
                                        break;

                                    case CALC_NUMBER_OCCURENCES:
                                        //Occurences occ= new Occurences();
                                        //results=occ.CalculateOccurences(logs, ) //searched element
                                        break;

                                    case CALC_VARIANCE:
                                        //Variance var=new Variance();
                                        //results=var.CalculateVariance(logs);
                                        break;

                                    case CALC_STANDARD_DEVIATION:
                                        //StandardDeviation stde=new StandardDeviation();
                                        //results=stde.CalculateStandardDeviation(logs);
                                        break;

                                    default:
                                        System.out.println("Incorrect option");
                                        break;
                                }
                                output.writeObject(results);
                            }
                        }
                        else
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
    
    private void sendRejectionMessage(String message) throws IOException {
        output.writeObject(new RejectionMessage(workerName, null, message));
    }


}