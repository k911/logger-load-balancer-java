package worker.server.config;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Optional;

public class WorkerServerConfiguration {

    private Optional<String> name;
    private Optional<InetAddress> inetAddress;
    private Optional<Integer> port;
    private Optional<Integer> serverThreadPoolSize;
    private Optional<WorkerConfiguration> workerConfiguration;
    private Optional<InetAddress> schedulerAddress;
    private Optional<Integer> schedulerPort;

    public WorkerServerConfiguration() {
    }

    public WorkerServerConfiguration(String name, InetAddress inetAddress, Integer port, Integer
            serverThreadPoolSize, WorkerConfiguration workerConfigurations, InetAddress schedulerAddress, Integer schedulerPort) {
        this.name = Optional.ofNullable(name);
        this.inetAddress = Optional.ofNullable(inetAddress);
        this.port = Optional.ofNullable(port);
        this.serverThreadPoolSize = Optional.ofNullable(serverThreadPoolSize);
        this.workerConfiguration = Optional.ofNullable(workerConfigurations);
        this.schedulerAddress = Optional.ofNullable(schedulerAddress);
        this.schedulerPort = Optional.ofNullable(schedulerPort);
    }

    public Optional<String> getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Optional.ofNullable(name);
    }

    public Optional<Integer> getPort() {
        return port;
    }

    public void setRandomPort() {
        try {
            ServerSocket socket = new ServerSocket(0);
            System.out.println("Setting random port: " + socket.getLocalPort());
            setPort(socket.getLocalPort());
        } catch (IOException e) {
            e.printStackTrace();
            setPort(0);
        }
    }

    public void setPort(Integer port) {
        this.port = Optional.ofNullable(port);
    }

    public Optional<WorkerConfiguration> getWorkerConfiguration() {
        return workerConfiguration;
    }

    public void setWorkerConfigurations(WorkerConfiguration workerConfiguration) {
        this.workerConfiguration = Optional.ofNullable(workerConfiguration);
    }

    public Optional<InetAddress> getInetAddress() {
        return inetAddress;
    }

    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = Optional.ofNullable(inetAddress);
    }

    public Optional<Integer> getServerThreadPoolSize() {
        return serverThreadPoolSize;
    }

    public void setServerThreadPoolSize(Integer serverThreadPoolSize) {
        this.serverThreadPoolSize = Optional.ofNullable(serverThreadPoolSize);
    }

    public Optional<InetAddress> getSchedulerAddress() {
        return schedulerAddress;
    }

    public void setSchedulerAddress(InetAddress schedulerAddress) {
        System.out.println("Setting scheduler address: " + schedulerAddress.getCanonicalHostName());
        this.schedulerAddress = Optional.ofNullable(schedulerAddress);
    }

    public Optional<Integer> getSchedulerPort() {
        return schedulerPort;
    }

    public void setSchedulerPort(Integer schedulerPort) {
        this.schedulerPort = Optional.ofNullable(schedulerPort);
    }

}
