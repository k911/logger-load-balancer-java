
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dotenv.Dotenv;
import items.Log;
import items.Worker;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ClientTest {
    public static void main(String args[]) {
        // Load environment variables
        new Dotenv(".env", false).load();

        Gson gson = new GsonBuilder()
                .serializeNulls()
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        int port = Integer.parseInt(System.getenv("SOCKET_SERVER_PORT"));
        String address = System.getenv("SOCKET_SERVER_HOST");
        try {
            Socket socket = new Socket(InetAddress.getByName(address), port);
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

            out.writeUTF("add_log");
            out.flush();

            Log log = new Log("test message", "{\"context\":\"lol\"}");
            out.writeObject(log);
            checkForFailure(in);

            log = (Log) in.readObject();
            System.out.println(log.getId());

            out.writeUTF("add_worker");
            Worker worker = new Worker(81, "localhost");
            out.writeObject(worker);
            checkForFailure(in);

            worker = (Worker) in.readObject();

            System.out.println(gson.toJson(worker));

            worker.addTask();
            worker.addTask();
            worker.addTask();

            out.writeUTF("update_worker");
            out.writeObject(worker);
            checkForFailure(in);

            worker = (Worker) in.readObject();

            System.out.println(gson.toJson(worker));

            worker.removeTask();
            worker.removeTask();

            out.writeUTF("update_worker");
            out.writeObject(worker);
            checkForFailure(in);

            worker = (Worker) in.readObject();

            System.out.println(gson.toJson(worker));

            out.writeUTF("exit");
            out.flush();
            out.close();

            socket.close();
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

