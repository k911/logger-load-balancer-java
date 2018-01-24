package connections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import items.Log;
import items.Worker;
import dotenv.Dotenv;
import controller.SendLogsController;

public class SchedulerConnection {
	private static SocketConnectionFactory socketConnectionFactory;
	private static Gson gson;
	private final static Logger logger = Logger.getLogger(SchedulerConnection.class.getName());

	public SchedulerConnection() {
		// Load .evn variables
		Dotenv.loadEnvironment();

		// Dependencies
		gson = buildGson();

		// Create a factory for socket connection
		socketConnectionFactory = new SocketConnectionFactory("SOCKET_SERVER_HOST", "SOCKET_SERVER_PORT");
	}

	public void getWorker() {
		try {
			Socket socket = socketConnectionFactory.make();
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

			out.writeUTF("get_worker");
			out.flush();
			checkForFailure(in);
			Worker worker = (Worker) in.readObject();

			out.writeUTF("exit");
			out.flush();
			out.close();
			socket.close();

		} catch (IOException | ClassNotFoundException err) {
			logger.severe("Error while trying to get a worker: " + err.getMessage());
		}
	}

	public void sendLogs(String message, String payload) {
		try {
			Socket socket = socketConnectionFactory.make();
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			
			// Create a new log
			Log log = new Log(message, payload);

			// Send log back to loadBalancer
			out.writeUTF("add_log");
			out.writeObject(log);
			out.flush();

			checkForFailure(in);
			
			// Print message and close connection
			logger.info("Success while addying a new log");
			SendLogsController.getController().setLogsStatusLabel("success", "Przesłano logi!!");
		
			out.writeUTF("exit");
			out.flush();
			out.close();
			socket.close();
		} catch (IOException|RuntimeException|ClassNotFoundException err) {
			SendLogsController.getController().setLogsStatusLabel("error", "Zapytanie nie mogło zostać zrealizowane: " + err.getMessage());
			logger.severe("Error while trying to post a log to loadBalancer: " + err.getMessage());
		}
	}

	private void checkForFailure(ObjectInputStream in) throws IOException, ClassNotFoundException {
		if (in.readUTF().equals("FAILURE")) {
			throw new RuntimeException(in.readUTF());
		}
	}

	private static Gson buildGson() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setDateFormat("yyyy-MM-dd hh:mm:ss.S");
		gsonBuilder.excludeFieldsWithoutExposeAnnotation();
		gsonBuilder.serializeNulls();
		return gsonBuilder.create();
	}
}
