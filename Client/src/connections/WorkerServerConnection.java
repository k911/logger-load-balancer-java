package connections;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import worker.communication.JobRequest;
import worker.communication.job.Job;

public class WorkerServerConnection {
    private final static Logger logger = Logger.getLogger(WorkerServerConnection.class.getName());
	private Socket socket;
	private JobRequest jobRequest;
	
	public WorkerServerConnection (String hostname, int port, Map<Long, Job> jobs) {
		try {
			socket = new Socket("localhost", port);
			jobRequest = new JobRequest("Author " + (new Date().getTime()), jobs);
		} catch (IOException err) {
			logConnectionIssue(err);
		}
	}
	
	private void logConnectionIssue(IOException err) {
		logger.severe("Problem with connecting to ServerWorker: " + err.getMessage());
	}
}
