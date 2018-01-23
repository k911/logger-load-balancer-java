package ServerWorker.src.worker.server.config;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class WorkerConfiguration {


    private Optional<String> name;
    private Optional<TimeUnit> timeoutUnit;
    private Optional<Integer> executorShutdownTimeout;
    private Optional<Integer> threadPoolSize;


    public WorkerConfiguration(String name, TimeUnit timeoutUnit, int executorShutdownTimeout, int threadPoolSize) {
        this.name = Optional.ofNullable(name);
        this.timeoutUnit = Optional.ofNullable(timeoutUnit);
        this.executorShutdownTimeout = Optional.ofNullable(executorShutdownTimeout);
        this.threadPoolSize = Optional.ofNullable(threadPoolSize);
    }

    public Optional<Integer> getExecutorShutdownTimeout() {
        return executorShutdownTimeout;
    }

    public Optional<String> getName() {
        return name;
    }

    public Optional<TimeUnit> getTimeoutUnit() {
        return timeoutUnit;
    }

    public Optional<Integer> getThreadPoolSize() {
        return threadPoolSize;
    }

}
