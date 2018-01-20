import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import database.MysqlConnectionFactory;
import database.MysqlDatabaseManager;
import database.SimpleStatementFactory;
import handler.DefaultHttpHandler;
import handler.HttpExceptionHandler;
import handler.LogsHttpHandler;
import repository.LogRepository;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.SQLException;

public class LoadBalancerServer {
    public static void main(String[] args) {
        String database = "database";
        MysqlConnectionFactory connectionFactory = new MysqlConnectionFactory("jdbc:mysql://127.0.0.1?characterEncoding=utf-8&useSSL=false", "root", "root");
        Connection connection = connectionFactory.make();
        SimpleStatementFactory statementFactory = new SimpleStatementFactory(connection);
        MysqlDatabaseManager databaseManager = new MysqlDatabaseManager(statementFactory, database);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyy-MM-dd hh:mm:ss.S");
        gsonBuilder.excludeFieldsWithoutExposeAnnotation();
        gsonBuilder.serializeNulls();
        Gson gson = gsonBuilder.create();

        boolean debug = true;
        if(debug | !databaseManager.exists()) {
            if (databaseManager.exists()) {
                databaseManager.drop();
                System.out.println("Database successfully dropped.");
            }
            databaseManager.create();
            System.out.println("Database successfully created.");
            databaseManager.executeUpdate("CREATE TABLE `database`.`logs` (`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT, `message` VARCHAR(255) NOT NULL, `context` LONGTEXT NOT NULL, `created_at` TIMESTAMP(3) NOT NULL, PRIMARY KEY (`id`)) ENGINE = InnoDB;");
            System.out.println("Schema successfully created.");
        }

        // Set database for connection
        try {
            connection.setCatalog(database);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        LogRepository logRepository = new LogRepository(databaseManager);

        InetSocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 8000);

        HttpExceptionHandler exceptionHandler = new HttpExceptionHandler(gson);
        DefaultHttpHandler defaultHandler = new DefaultHttpHandler(exceptionHandler);
        LogsHttpHandler logsHttpHandler = new LogsHttpHandler(exceptionHandler, defaultHandler, logRepository, gson);

        try {
            HttpServer httpServer = HttpServer.create(socketAddress, 0);
            httpServer.createContext("/logs", logsHttpHandler);
            httpServer.createContext("/", defaultHandler);
            httpServer.setExecutor(null);
            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        try {
//            connection.close();
//        } catch (SQLException e) {
//            System.out.println("Could not close database connection: " + e.getLocalizedMessage());
//        }
    }
}
