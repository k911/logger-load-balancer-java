package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MysqlConnectionFactory implements ConnectionFactory {

    private static final String driver = "com.mysql.jdbc.Driver";

    @Override
    public Connection make(String uri, String username, String password) {

        if (!driverExists(driver)) {
            throw new RuntimeException("MySQL JDBC Driver is required to create connection.");
        }

        try {
            return DriverManager.getConnection(uri, username, password);
        } catch (SQLException e) {
            System.out.println("Unable to create connection: " + e.getLocalizedMessage());
            System.exit(1);
        }

        return null;
    }


    private boolean driverExists(String driver) {
        try {
            Class.forName(driver);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}
