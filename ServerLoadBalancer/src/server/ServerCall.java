package server;

import database.ConnectionFactory;
import database.MysqlDatabaseManager;
import database.SimpleStatementFactory;
import database.StatementFactory;
import items.GetLogsCommand;
import items.HttpException;
import items.Log;
import items.Worker;
import repository.LogRepository;
import repository.WorkerRepository;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.util.Collection;
import java.util.concurrent.Callable;

public class ServerCall implements Callable<String> {
    private Socket socket;
    private MysqlDatabaseManager databaseManager;
    private FutureTaskCallback<String> ft;
    private int connectionId;
    private WorkerRepository workerRepository;
    private LogRepository logRepository;
    private static final String SUCCESS = "SUCCESS";
    private static final String FAILURE = "FAILURE";

    public ServerCall(Socket socket, ConnectionFactory connectionFactory, int connectionId) {
        String database = System.getenv("DATABASE_NAME");
        this.connectionId = connectionId;
        Connection privateConnection = connectionFactory.make(database);
        StatementFactory statementFactory = new SimpleStatementFactory(privateConnection);
        this.socket = socket;
        this.databaseManager = new MysqlDatabaseManager(statementFactory, database);
        this.ft = new FutureTaskCallback<>(this, privateConnection);
    }

    public FutureTaskCallback<String> getFt() {
        return ft;
    }

    @Override
    public String call() {
        String txt = socket.getInetAddress().getHostName();
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            action_loop:
            while (true) {
                System.out.println("[" + this.connectionId + "] Waiting for command..");
                String command = in.readUTF();
                System.out.println("[" + this.connectionId + "] Command: " + command);

                try {
                    switch (command) {
                        case "add_log":
                            Log log = (Log) in.readObject();
                            getLogRepository().add(log);
                            out.writeUTF(SUCCESS);
                            out.writeObject(log);
                            break;
                        case "count_logs":
                            out.writeUTF(SUCCESS);
                            out.writeInt(getLogRepository().count());
                            break;
                        case "get_logs":
                            GetLogsCommand getLogsCommand = (GetLogsCommand) in.readObject();
                            Collection<Log> logs = getLogRepository().findAll(getLogsCommand.getOffset(), getLogsCommand.getLimit());
                            out.writeUTF(SUCCESS);
                            out.writeObject(logs);
                            break;
                        case "get_worker":
                            out.writeUTF(SUCCESS);
                            out.writeObject(getWorkerRepository().first());
                            break;
                        case "update_worker":
                            Worker worker = (Worker) in.readObject();
                            getWorkerRepository().update(worker);
                            out.writeUTF(SUCCESS);
                            out.writeObject(worker);
                            break;
                        case "add_worker":
                            Worker newWorker = (Worker) in.readObject();
                            getWorkerRepository().add(newWorker);
                            out.writeUTF(SUCCESS);
                            out.writeObject(newWorker);
                            break;
                        case "exit":
                            break action_loop;
                        default:
                            System.out.println("[" + this.connectionId + "] No such command: " + command);
                            break;
                    }
                } catch (HttpException e) {
                    out.writeUTF(FAILURE);
                    out.writeUTF(e.getMessage());
                    System.err.println(e.getMessage());
                }

                out.flush();
            }
            socket.close();
        } catch (EOFException e) {
            System.err.println("Unexpected end of stream");
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Error while closing socket on error.");
            }
        }

        return "[" + this.connectionId + "] Socket " + txt + " is closed.";
    }

    private WorkerRepository getWorkerRepository() {
        if (workerRepository == null) {
            workerRepository = new WorkerRepository(databaseManager);
        }

        return workerRepository;
    }

    private LogRepository getLogRepository() {
        if (logRepository == null) {
            logRepository = new LogRepository(databaseManager);
        }

        return logRepository;
    }
}