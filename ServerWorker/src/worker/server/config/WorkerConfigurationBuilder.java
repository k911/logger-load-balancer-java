package worker.server.config;

import java.util.concurrent.TimeUnit;

public class WorkerConfigurationBuilder {

    private String name = "Worker";
    private TimeUnit timeoutUnit = TimeUnit.SECONDS;
    private int executorShutdownTimeout = 3;
    private int threadPoolSize = 5;

    public WorkerConfigurationBuilder() {
    }

    public WorkerConfiguration buildWorkerConfiguration() {
        return new WorkerConfiguration(name, timeoutUnit, executorShutdownTimeout, threadPoolSize);
    }

    public WorkerConfigurationBuilder name(String name) {
        this.name = name;
        return this;
    }

    public WorkerConfigurationBuilder timeoutUnit(TimeUnit timeoutUnit) {
        this.timeoutUnit = timeoutUnit;
        return this;
    }

    public WorkerConfigurationBuilder executorShutdownTimeout(int executorShutdownTimeout) {
        this.executorShutdownTimeout = executorShutdownTimeout;
        return this;
    }

    public WorkerConfigurationBuilder threadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
        return this;
    }
}
