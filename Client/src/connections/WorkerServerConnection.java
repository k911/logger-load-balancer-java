package connections;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

public class WorkerServerConnection {
    private final static Logger logger = Logger.getLogger(WorkerServerConnection.class.getName());
	private Socket socket;
	
	public WorkerServerConnection (String hostname, int port) {
		try {
			this.socket = new Socket("localhost", port);
		} catch (IOException err) {
			logConnectionIssue(err);
		}
	}
	
	private void logConnectionIssue(IOException err) {
		logger.severe("Problem with connecting to ServerWorker: " + err.getMessage());
	}
}
