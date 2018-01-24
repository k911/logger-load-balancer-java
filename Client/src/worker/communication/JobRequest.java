package worker.communication;

import worker.communication.job.Job;


import java.io.Serializable;
import java.util.Map;

public class JobRequest extends SocketMessage implements Serializable {
    private static final long serialVersionUID = -1834696253225109484L;

    private Map<Long,Job> jobs; //key: unique id

    public JobRequest(String author, Map<Long,Job> jobs) {
        super(author);
        this.jobs = jobs;
    }

    @Deprecated
    public JobRequest() {
    }

    public Map<Long,Job> getJobs() {
        return jobs;
    }

    public void setJobs(Map<Long,Job> jobs) {
        this.jobs = jobs;
    }
}
