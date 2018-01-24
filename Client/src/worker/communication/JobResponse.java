package worker.communication;


import worker.communication.job.Results;

import java.io.Serializable;
import java.util.Map;

public class JobResponse extends SocketMessage implements Serializable {
    private static final long serialVersionUID = -4789673396552721734L;

    private Map<Long,Results> results;

    public JobResponse(String author, Map<Long,Results> results) {
        super(author);
        this.results = results;
    }

    @Deprecated
    public JobResponse() {
    }

    public Map<Long,Results> getResults() {
        return results;
    }

    public void setResults(Map<Long,Results> results) {
        this.results = results;
    }
}
