import database.MysqlDatabaseManager;
import database.MysqlConnectionFactory;
import database.SimpleStatementFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class LoadBalancerServer {
    public static void main(String[] args) {
        String database = "database";
        MysqlConnectionFactory connectionFactory = new MysqlConnectionFactory();
        Connection connection = connectionFactory.make("jdbc:mysql://127.0.0.1", "root", "root");
        SimpleStatementFactory statementFactory = new SimpleStatementFactory(connection);
        MysqlDatabaseManager databaseManager = new MysqlDatabaseManager(statementFactory, database);

        if(databaseManager.exists()) {
            databaseManager.drop();
            System.out.println("Database successfully dropped.");
        }

        databaseManager.create();
        System.out.println("Database successfully created.");
        databaseManager.executeUpdate("CREATE TABLE `database`.`logs` (`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT, `message` VARCHAR(255) NOT NULL, `attributes` LONGTEXT NOT NULL, `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (`id`)) ENGINE = InnoDB;");
        System.out.println("Schema successfully created.");

        // Set database for connection
        try {
            connection.setCatalog(database);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println("Could not close database connection: " + e.getLocalizedMessage());
        }
    }
}
