package worker.server;

import worker.server.config.WorkerConfiguration;
import worker.server.config.WorkerServerConfiguration;
import worker.server.worker.Worker;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


public class WorkerServer implements Runnable {


    private final static Logger logger = Logger.getLogger(WorkerServer.class.getName());
    ExecutorService executors;

    private List<WorkerConfiguration> workerConfigurations = new ArrayList<>
            (Arrays.asList(new WorkerConfiguration("Worker", TimeUnit.SECONDS, 10, 3)));
    private String name = "Worker-Server";
    private int workerPoolSize = 30;
    private InetAddress inetAddress;
    private Integer port = 8080;
    private ThreadSafeSet<String> connectedUsers = new ThreadSafeSet<>();
    private WorkerServerConfiguration workerServerConfiguration;
    private volatile boolean shouldLive=true;


    public WorkerServer(WorkerServerConfiguration configuration) {
        this.workerServerConfiguration = configuration;

        logger.info("WorkerServer is being configured");

        this.configure(configuration);
        logger.info("WorkerServer is configured");

        logger.info("Created WorkerServer with name: " + this.name + " address:" + this.inetAddress.getHostAddress());

    }

    private void configure(WorkerServerConfiguration configuration) {

        if (configuration.getName().isPresent())
            this.name = configuration.getName().get();

        if (configuration.getWorkerConfigurations().isPresent())
            workerConfigurations = configuration.getWorkerConfigurations().get();

        if (configuration.getServerThreadPoolSize().isPresent())
            workerPoolSize = configuration.getServerThreadPoolSize().get();

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
            this.port = configuration.getPort().get();


    }

    @Override
    public void run() {
        logger.info("Server " + this.name + " thread is started: " + Thread.currentThread().getName());

        logger.info("Request Handler is starting");
        handleRequests();



    }

    public void stop(){
        shouldLive=false;
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
                executors.submit(new Worker(client));
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
