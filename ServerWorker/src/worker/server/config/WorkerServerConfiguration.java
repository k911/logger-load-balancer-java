package worker.server.config;

import java.net.InetAddress;
import java.util.List;
import java.util.Optional;

public class WorkerServerConfiguration {

    private Optional<String> name;
    private Optional<InetAddress> inetAddress;
    private Optional<Integer> port;
    private Optional<Integer> serverThreadPoolSize;
    private Optional<List<WorkerConfiguration>> workerConfigurations;
    private DBConnectionConfiguration dbConnectionConfiguration;
    private DaoConfiguration daoConfiguration;

    public WorkerServerConfiguration() {
    }

    public WorkerServerConfiguration(String name, InetAddress inetAddress, Integer port, Integer
            serverThreadPoolSize, List<WorkerConfiguration> workerConfigurations) {
        this.name = Optional.ofNullable(name);
        this.inetAddress = Optional.ofNullable(inetAddress);
        this.port = Optional.ofNullable(port);
        this.serverThreadPoolSize = Optional.ofNullable(serverThreadPoolSize);
        this.workerConfigurations = Optional.ofNullable(workerConfigurations);
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

    public void setPort(Integer port) {
        this.port = Optional.ofNullable(port);
    }

    public Optional<List<WorkerConfiguration>> getWorkerConfigurations() {
        return workerConfigurations;
    }

    public void setWorkerConfigurations(List<WorkerConfiguration> workerConfigurations) {
        this.workerConfigurations = Optional.ofNullable(workerConfigurations);
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

    public DBConnectionConfiguration getDbConnectionConfiguration() {
        return dbConnectionConfiguration;
    }

    public void setDbConnectionConfiguration(DBConnectionConfiguration dbConnectionConfiguration) {
        this.dbConnectionConfiguration = dbConnectionConfiguration;
    }

    public DaoConfiguration getDaoConfiguration() {
        return daoConfiguration;
    }

    public void setDaoConfiguration(DaoConfiguration daoConfiguration) {
        this.daoConfiguration = daoConfiguration;
    }
}
