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

	public Worker getWorker() {
		try {
			Socket socket = socketConnectionFactory.make();
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

			// Send a request for worker data
			out.writeUTF("get_worker");
			out.flush();
			
			// Check if request did not failed
			checkForFailure(in);
		
			// Read the worker
			Worker worker = (Worker) in.readObject();
			
			// Close connection
			out.writeUTF("exit");
			out.flush();
			out.close();
			socket.close();
			
			return worker;

		} catch (IOException | ClassNotFoundException err) {
			logger.severe("Error while trying to get a worker: " + err.getMessage());
		}
		
		return null;
	}

	public void sendLogs(String message, String context) {
		try {
			Socket socket = socketConnectionFactory.make();
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			
			// Create a new log
			Log log = new Log(message, context);

			// Send log back to loadBalancer
			out.writeUTF("add_log");
			out.writeObject(log);
			out.flush();
			checkForFailure(in);
			log = (Log) in.readObject();
			
			// Print message and close connection
			logger.info("Success while adding a new log (ID: " + log.getId() + ")");
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
