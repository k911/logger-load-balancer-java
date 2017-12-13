import com.sun.net.httpserver.HttpServer;
import database.MysqlConnectionFactory;
import database.MysqlDatabaseManager;
import database.SimpleStatementFactory;
import handler.DefaultHandler;
import handler.LogInputHandler;
import repository.LogRepository;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.SQLException;

public class LoadBalancerServer {
    public static void main(String[] args) {
        String database = "database";
        MysqlConnectionFactory connectionFactory = new MysqlConnectionFactory("jdbc:mysql://127.0.0.1", "root", "root");
        Connection connection = connectionFactory.make();
        SimpleStatementFactory statementFactory = new SimpleStatementFactory(connection);
        MysqlDatabaseManager databaseManager = new MysqlDatabaseManager(statementFactory, database);

        boolean debug = true;
        if(!databaseManager.exists() || debug) {
            if (databaseManager.exists()) {
                databaseManager.drop();
                System.out.println("Database successfully dropped.");
            }
            databaseManager.create();
            System.out.println("Database successfully created.");
            databaseManager.executeUpdate("CREATE TABLE `database`.`logs` (`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT, `message` VARCHAR(255) NOT NULL, `attributes` LONGTEXT NOT NULL, `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (`id`)) ENGINE = InnoDB;");
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
        DefaultHandler defaultHandler = new DefaultHandler();
        LogInputHandler logInputHandler = new LogInputHandler(defaultHandler, logRepository);

        try {
            HttpServer httpServer = HttpServer.create(socketAddress, 0);
            httpServer.createContext("/logs", logInputHandler);
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
