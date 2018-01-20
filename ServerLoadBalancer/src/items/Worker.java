package items;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class Worker implements Serializable {

    @Expose
    private int id = -1;

    @Expose
    private int taskCount = 0;

    @Expose
    private int port = -1;

    @Expose
    private String host;

    public Worker(int id, int taskCount, int port, String host) {
        this.id = id;
        this.taskCount = taskCount;
        this.port = port;
        this.host = host;
    }

    public Worker(int port, String host) {
        this.port = port;
        this.host = host;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new RuntimeException("Log id must be an unsigned integer.");
        }

        this.id = id;
    }

    public boolean hasPort() {
        return port != -1;
    }

    public boolean hasHost() {
        return host != null && !host.isEmpty();
    }

    public int getTaskCount() {
        return taskCount;
    }

    public void addTask() {
        ++taskCount;
    }

    public void removeTask() {
        if(taskCount < 1) {
            throw new RuntimeException("Could not remove tasks, because there is none");
        }

        --taskCount;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }
}
