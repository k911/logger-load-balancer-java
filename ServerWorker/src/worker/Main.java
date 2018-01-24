package worker;

import dotenv.Dotenv;
import worker.server.WorkerServer;
import worker.server.config.WorkerConfiguration;
import worker.server.config.WorkerConfigurationBuilder;
import worker.server.config.WorkerServerConfiguration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

public class Main {

    private final static Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        // Load environment variables
        Dotenv.loadEnvironment();

        WorkerServerConfiguration serverConfiguration = prepareConfig();

        logger.info("Starting the WorkServer " + serverConfiguration.getName());

        WorkerServer workerServer = new WorkerServer(serverConfiguration);

        Thread server=new Thread(workerServer);
        server.run();



        try {
            server.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        System.out.println("Bye");

    }

    private static WorkerServerConfiguration prepareConfig() {


        WorkerServerConfiguration serverConfiguration = new WorkerServerConfiguration();
        serverConfiguration.setName("WorkerServer-1");

        /*--------------------------------------------------------------------------------------------------------------
        worker server address and port
         */

        try {
            serverConfiguration.setInetAddress(InetAddress.getByName(System.getenv("WORKER_HOST")));
        } catch (UnknownHostException e) {
            logger.severe("Configuration setup failed. Could not resolve InetAddress "
                    + e.getMessage());
        }

        // Set random ports to create multiple workers
        if(System.getenv("APP_ENV").equals("dev")) {
            serverConfiguration.setRandomPort();
        } else {
            serverConfiguration.setPort(Integer.parseInt(System.getenv("WORKER_PORT")));
        }

        /*--------------------------------------------------------------------------------------------------------------
        number of  maximum runnable workers
         */
        serverConfiguration.setServerThreadPoolSize(Integer.parseInt(System.getenv("WORKER_THREAD_POOL_SIZE")));
        /*--------------------------------------------------------------------------------------------------------------
        scheduler address and port:
         */
        try {
            serverConfiguration.setSchedulerAddress(InetAddress.getByName(System.getenv("SOCKET_SERVER_HOST")));
        } catch (UnknownHostException e) {
            logger.severe("Configuration setup failed. Could not resolve InetAddress "
                    + e.getMessage());
        }
        serverConfiguration.setSchedulerPort(Integer.parseInt(System.getenv("SOCKET_SERVER_PORT")));

        System.out.println("Scheduler port: " + serverConfiguration.getSchedulerPort().get());
        System.out.println("Worker port: " + serverConfiguration.getPort().get());

        /*--------------------------------------------------------------------------------------------------------------
        RunnableWorker configuration - same for every worker
         */
        WorkerConfiguration workerConfiguration = new WorkerConfigurationBuilder().name("RunnableWorker-Calc")
                .threadPoolSize(5).buildWorkerConfiguration();


        serverConfiguration.setWorkerConfigurations(workerConfiguration);


        return serverConfiguration;
    }

}
