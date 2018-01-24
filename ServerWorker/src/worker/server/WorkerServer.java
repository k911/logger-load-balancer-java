package worker.server;

import worker.communication.scheduler.Worker;
import worker.server.config.WorkerConfiguration;
import worker.server.config.WorkerServerConfiguration;
import worker.server.worker.RunnableWorker;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.*;
import java.util.logging.Logger;


public class WorkerServer implements Runnable {


    private final static Logger logger = Logger.getLogger(WorkerServer.class.getName());
    ExecutorService executors;

    private WorkerConfiguration workerConfiguration;
    private String name;
    private int workerPoolSize;
    private InetAddress inetAddress;
    private Integer port;
    private ThreadSafeSet<String> connectedUsers = new ThreadSafeSet<>();
    private WorkerServerConfiguration workerServerConfiguration;
    private volatile boolean shouldLive = true;
    private InetAddress schedulerAddress;
    private Integer schedulerPort;


    public WorkerServer(WorkerServerConfiguration configuration) {
        this.workerServerConfiguration = configuration;

        logger.info("WorkerServer is being configured");

        this.configure(configuration);
        logger.info("WorkerServer is configured");

        logger.info("Created WorkerServer with name: " + this.name + " address:" + this.inetAddress.getHostAddress());

    }

    private void configure(WorkerServerConfiguration configuration) {


            this.name = configuration.getName().orElse("RunnableWorker-Server");


            workerConfiguration = configuration.getWorkerConfiguration()
                    .orElse(new WorkerConfiguration("RunnableWorker", TimeUnit.SECONDS, 10, 3));


            workerPoolSize = configuration.getServerThreadPoolSize().orElse(30);

        if (configuration.getInetAddress().isPresent())
            this.inetAddress = configuration.getInetAddress().get();
        else {
            try {
                this.inetAddress = InetAddress.getByName("127.0.0.1");
            } catch (UnknownHostException e) {
                logger.severe("Unable to create InetAddress:" + e.getMessage());
            }
        }
        if (configuration.getPort().isPresent())
            this.port = configuration.getPort().orElse(8080);

        if(configuration.getSchedulerAddress().isPresent())
            this.schedulerAddress=configuration.getInetAddress().get();
        else{
            try {
                this.inetAddress = InetAddress.getByName("127.0.0.1");
            } catch (UnknownHostException e) {
                logger.severe("Unable to create InetAddress:" + e.getMessage());
            }
        }

        if (configuration.getSchedulerPort().isPresent())
            this.schedulerPort = configuration.getSchedulerPort().orElse(80);


    }

    @Override
    public void run() {
        logger.info("Server " + this.name + " thread is started: " + Thread.currentThread().getName());


        Future<Boolean> future=null;
        try {

            ExecutorService e = Executors.newSingleThreadExecutor();
            Callable<Boolean> registrationTask = ()->registerWorker();
            future = e.submit(registrationTask);
            if(!future.get(15, TimeUnit.SECONDS))
            {
                logger.severe("Worker Server registration failed! Server will shut down");
                return;
            }

        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            logger.severe(e.getMessage());
            logger.severe("Server could not register with Scheduler due to the timeout - shutting down");
            future.cancel(true);
            return;
        }


        logger.info("Request Handler is starting");
        handleRequests();


    }

    public void stop() {
        shouldLive = false;
    }


    private boolean registerWorker(){
        try{
            logger.info("Registering Worker Server: "+this.name+" with Scheduler on "+this.schedulerAddress.getHostAddress());
            Socket scheduler=new Socket(schedulerAddress.getHostAddress(),schedulerPort);

            ObjectOutputStream output = new ObjectOutputStream(scheduler.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(scheduler.getInputStream());

            Worker worker = new Worker(this.port, this.inetAddress.getHostAddress());
            output.writeUTF("add_worker");
            output.writeObject(worker);
            String response=input.readUTF();
            logger.info("Registration result:"+response);
            return response.equals("SUCCESS");

        } catch (UnknownHostException e) {
            logger.warning(e.getMessage());
            return false;
        } catch (IOException e) {
            logger.warning(e.getMessage());
            return false;
        }


    }


    private void handleRequests() {


        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            logger.warning(name + " " + e.getMessage());

        }

        executors = Executors.newFixedThreadPool(workerPoolSize);

        while (shouldLive) {

            logger.info("WorkerServer " + this.name + " is waiting for client");
            try {
                Socket client = serverSocket.accept();
                executors.submit(new RunnableWorker(client, workerConfiguration, connectedUsers));
            } catch (IOException e) {
                logger.warning("Exception caught when handling socket " + e.getMessage());
                return;
            }
        }

        executors.shutdown();


    }

    public String getName() {
        return name;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public Integer getPort() {
        return port;
    }

    public long getConnectedUserCount() {
        return connectedUsers.size();
    }
}
