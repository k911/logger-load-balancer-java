package worker.communication.job;

import java.io.Serializable;

public class Results implements Serializable{
    private static final long serialVersionUID = -104389913524571836L;

    private String result;

    public Results(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
