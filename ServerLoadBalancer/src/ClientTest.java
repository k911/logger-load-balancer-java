
import com.google.gson.Gson;
import items.Log;
import items.Worker;
import server.SocketConnectionFactory;
import utils.AppUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientTest {
    public static void main(String args[]) {
        // Load environment variables
        AppUtils.loadEnvironment();

        // Dependencies
        Gson gson = AppUtils.buildGson();
        SocketConnectionFactory socketConnectionFactory = new SocketConnectionFactory("SOCKET_SERVER_HOST", "SOCKET_SERVER_PORT");

        try {
            Socket socket = socketConnectionFactory.make();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

            // Add log
            Log log = new Log("test message", "{\"context\":\"lol\"}");
            out.writeUTF("add_log");
            out.writeObject(log);
            out.flush();
            checkForFailure(in);

            log = (Log) in.readObject();
            System.out.println(log.getId());

            // Register worker
            Worker worker = new Worker(81, "localhost");
            out.writeUTF("add_worker");
            out.writeObject(worker);
            out.flush();
            checkForFailure(in);
            worker = (Worker) in.readObject();
            System.out.println(gson.toJson(worker));

            worker.addTask();
            worker.addTask();
            worker.addTask();

            // Update worker status
            out.writeUTF("update_worker");
            out.writeObject(worker);
            out.flush();
            checkForFailure(in);
            worker = (Worker) in.readObject();
            System.out.println(gson.toJson(worker));

            worker.removeTask();
            worker.removeTask();

            out.writeUTF("update_worker");
            out.writeObject(worker);
            out.flush();
            checkForFailure(in);
            worker = (Worker) in.readObject();
            System.out.println(gson.toJson(worker));

            // Second socket
            Socket socket2 = socketConnectionFactory.make();
            ObjectInputStream in2 = new ObjectInputStream(socket2.getInputStream());
            ObjectOutputStream out2 = new ObjectOutputStream(socket2.getOutputStream());

            out2.writeUTF("get_worker");
            out2.flush();
            checkForFailure(in2);
            worker = (Worker) in2.readObject();
            System.out.println(gson.toJson(worker));

            out.writeUTF("exit");
            out.flush();
            out.close();

            out2.writeUTF("exit");
            out2.flush();
            out2.close();

            socket.close();
            socket2.close();

        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private static void checkForFailure(ObjectInputStream in) throws IOException {
        if (in.readUTF().equals("FAILURE")) {
            throw new RuntimeException(in.readUTF());
        }
    }
}

