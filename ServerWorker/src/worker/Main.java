package worker;

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

        WorkerServerConfiguration serverConfiguration = prepareConfig();

        logger.info("Starting the WorkServer " + serverConfiguration.getName());

        WorkerServer workerServer = new WorkerServer(serverConfiguration);

        workerServer.run();

    }

    public static WorkerServerConfiguration prepareConfig() {

        WorkerServerConfiguration serverConfiguration = new WorkerServerConfiguration();
        serverConfiguration.setName("WorkerServer-1");
        try {
            serverConfiguration.setInetAddress(InetAddress.getLocalHost());
        } catch (UnknownHostException e) {
            logger.severe("Configuration setup failed. Could not resolve InetAddress "
                    + e.getMessage());
        }
        serverConfiguration.setPort(8082);
        serverConfiguration.setServerThreadPoolSize(30);

        try {
            serverConfiguration.setSchedulerAddress(InetAddress.getLocalHost());
        } catch (UnknownHostException e) {
            logger.severe("Configuration setup failed. Could not resolve InetAddress "
                    + e.getMessage());
        }
        serverConfiguration.setSchedulerPort(80);


        WorkerConfiguration workerConfiguration = new WorkerConfigurationBuilder().name("Worker-Calc")
                .threadPoolSize(5).buildWorkerConfiguration();

        serverConfiguration.setWorkerConfigurations(workerConfiguration);


        return serverConfiguration;
    }

}
