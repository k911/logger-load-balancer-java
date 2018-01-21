import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import database.MysqlConnectionFactory;
import database.MysqlDatabaseManager;
import database.SimpleStatementFactory;
import handler.*;
import repository.LogRepository;
import repository.WorkerRepository;
import server.ServerCall;
import utils.AppUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoadBalancerServer {

    public static void main(String[] args) {
        // Load environment variables
        AppUtils.loadEnvironment();

        boolean debug = Boolean.valueOf(System.getenv("APP_DEBUG"));

        // Load servers configuration
        String httpServerHost = System.getenv("HTTP_SERVER_HOST");
        int httpServerPort = Integer.parseInt(System.getenv("HTTP_SERVER_PORT"));
        String socketServerHost = System.getenv("SOCKET_SERVER_HOST");
        int socketServerPort = Integer.parseInt(System.getenv("SOCKET_SERVER_PORT"));

        if (httpServerHost.equals(socketServerHost) && httpServerPort == socketServerPort) {
            throw new RuntimeException("Could not start servers, because their hosts and ports are the same!");
        }

        String database = System.getenv("DATABASE_NAME");
        MysqlConnectionFactory connectionFactory = new MysqlConnectionFactory(System.getenv("DATABASE_URI"), System.getenv("DATABASE_USER"), System.getenv("DATABASE_PASSWORD"));
        Connection connection = connectionFactory.make();
        SimpleStatementFactory statementFactory = new SimpleStatementFactory(connection);
        MysqlDatabaseManager databaseManager = new MysqlDatabaseManager(statementFactory, database);


        if (debug | !databaseManager.exists()) {
            if (databaseManager.exists()) {
                databaseManager.drop();
                System.out.println("Database successfully dropped.");
            }
            databaseManager.create();
            setDatabase(database, connection);
            System.out.println("Database successfully created.");
            createDatabase(databaseManager);
            System.out.println("Schema successfully created.");
        } else {
            setDatabase(database, connection);
        }

        // Dependencies
        Gson gson = AppUtils.buildGson();
        LogRepository logRepository = new LogRepository(databaseManager);
        WorkerRepository workerRepository = new WorkerRepository(databaseManager);

        // Http server handlers
        HttpExceptionHandler exceptionHandler = new HttpExceptionHandler(gson);
        DefaultHttpHandler defaultHandler = new DefaultHttpHandler(exceptionHandler);
        LogsHttpHandler logsHttpHandler = new LogsHttpHandler(exceptionHandler, defaultHandler, logRepository, gson);
        WorkersHttpHandler workersHttpHandler = new WorkersHttpHandler(exceptionHandler, defaultHandler, workerRepository, gson);
        WorkerFirstHttpHandler workerFirstHttpHandler = new WorkerFirstHttpHandler(exceptionHandler, defaultHandler, workerRepository, gson);

        // Load Http Server
        InetSocketAddress socketAddress = new InetSocketAddress(httpServerHost, httpServerPort);
        HttpServer httpServer = null;
        try {
            httpServer = HttpServer.create(socketAddress, 0);
            httpServer.createContext("/logs", logsHttpHandler);
            httpServer.createContext("/workers", workersHttpHandler);
            httpServer.createContext("/workers/first", workerFirstHttpHandler);
            httpServer.createContext("/", defaultHandler);
            httpServer.setExecutor(null);
            httpServer.start();
            System.out.println("Http server started successfully. Listens on: " + httpServerHost + ":" + httpServerPort);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Load socket server
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(socketServerPort, 50, InetAddress.getByName(socketServerHost));
            ExecutorService exec = Executors.newCachedThreadPool();
            int connectionId = 0;
            System.out.println("Socket server started successfully. Listens on: " + socketServerHost + ":" + socketServerPort);
            while (true) {
                Socket socket = serverSocket.accept();
                exec.execute(new ServerCall(socket, connectionFactory, connectionId).getFt());
                ++connectionId;
            }

        } catch (IOException e) {
            System.out.println("Could not create server socket on port: " + socketServerPort + ".");
            e.printStackTrace();
        } finally {
            if (null != serverSocket) {
                try {
                    serverSocket.close();
                    connection.close();

                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                }
            }

            if (null != httpServer) {
                httpServer.stop(1);
            }
        }
    }

    private static void createDatabase(MysqlDatabaseManager databaseManager) {
        databaseManager.executeUpdate("CREATE TABLE `logs` (`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT, `message` VARCHAR(255) NOT NULL, `context` LONGTEXT NOT NULL, `created_at` TIMESTAMP(3) NOT NULL, PRIMARY KEY (`id`)) ENGINE = InnoDB;");
        databaseManager.executeUpdate("CREATE TABLE `workers` ( `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT , `tasks` INT(10) UNSIGNED NOT NULL , `host` VARCHAR(255) NOT NULL , `port` INT UNSIGNED NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;");
        databaseManager.executeUpdate("ALTER TABLE `workers` ADD UNIQUE( `host`, `port`);");
    }

    private static void setDatabase(String database, Connection connection) {
        // Set database for connection
        try {
            connection.setCatalog(database);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
