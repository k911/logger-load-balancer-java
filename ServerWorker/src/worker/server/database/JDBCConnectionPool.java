package worker.server.database;

import worker.server.ObjectPool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCConnectionPool extends ObjectPool<Connection> {

    private String dsn, usr, pwd;

    public JDBCConnectionPool(String driver, String dsn, String usr, String pwd) {
        super();
        try {
            Class.forName(driver).newInstance();
        } catch (Exception e) {
            System.out.println("JDBCConnectionPool exception caught:" + e);
        }
        this.dsn = dsn;
        this.usr = usr;
        this.pwd = pwd;
    }

    @Override
    protected Connection create() {
        try {
            System.out.println("Creating new Connection");
            return (DriverManager.getConnection(dsn, usr, pwd));
        } catch (SQLException e) {
            System.out.println("JDBCConnectionPool exception caught:" + e);
            return (null);
        }
    }

    @Override
    public void expire(Connection o) {
        try {
            ((Connection) o).close();
        } catch (SQLException e) {
            System.out.println("JDBCConnectionPool exception caught:" + e);
        }
    }

    @Override
    public boolean validate(Connection o) {
        try {
            return (!((Connection) o).isClosed());
        } catch (SQLException e) {
            System.out.println("JDBCConnectionPool exception caught:" + e);
            return (false);
        }
    }
}