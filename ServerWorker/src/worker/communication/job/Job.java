package worker.communication.job;

import java.io.Serializable;
import java.util.List;

public class Job implements Serializable{
    private static final long serialVersionUID = -7364705581260257331L;

    private JobType jobType;
    private List<String> argument;

    @Deprecated
    public Job(){}

    public Job(JobType jobType, List<String> argument) {
        this.jobType = jobType;
        this.argument = argument;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public List<String> getArgument() {
        return argument;
    }

    public void setArgument(List<String> argument) {
        this.argument = argument;
    }
}
