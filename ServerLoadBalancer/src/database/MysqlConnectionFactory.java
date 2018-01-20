package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MysqlConnectionFactory implements ConnectionFactory {

    private static final String driver = "com.mysql.jdbc.Driver";

    private String uri;
    private String username;
    private String password;

    public MysqlConnectionFactory(String uri, String username, String password) {

        this.uri = uri;
        this.username = username;
        this.password = password;
    }

    @Override
    public Connection make() {
        System.out.println("INFO: new MySQL connection created");

        if (!driverExists(driver)) {
            throw new RuntimeException("MySQL JDBC Driver is required to create connection.");
        }

        try {
            return DriverManager.getConnection(this.uri, this.username, this.password);
        } catch (SQLException e) {
            System.out.println("Unable to create connection: " + e.getLocalizedMessage());
            System.exit(1);
        }

        return null;
    }

    @Override
    public Connection make(String database) {
        Connection connection = make();

        if(null != connection) {
            try {
                connection.setCatalog(database);
                return connection;
            } catch (SQLException e) {
                System.out.println("Unable to create connection: " + e.getLocalizedMessage() + " with database: " + database);
            }
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
