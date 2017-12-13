package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MysqlDatabaseManager {
    private StatementFactory statementFactory;
    private String database;

    public MysqlDatabaseManager(StatementFactory statementFactory, String database) {

        this.statementFactory = statementFactory;
        this.database = database;
    }

    public PreparedStatement prepareStatement(String sql) {
        return statementFactory.prepare(sql);
    }

    public ResultSet executeQuery(String sql) {
        Statement statement = this.statementFactory.make();

        try {
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            System.out.println("Error during executing query: " + e.getLocalizedMessage() + ". SQL Query: " + sql);
        }

        return null;
    }

    public void executeUpdate(String sql) {
        Statement statement = this.statementFactory.make();

        try {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println("Error during executing query: " + e.getLocalizedMessage() + ". SQL Query: " + sql);
        }
    }

    public void drop() {
        Statement statement = this.statementFactory.make();
        try {
            statement.executeUpdate("DROP DATABASE `" + this.database + "`");
        } catch (SQLException e) {
            System.out.println("Could not drop database: " + e.getLocalizedMessage());
        }
    }

    public void create() {
        Statement statement = this.statementFactory.make();
        try {
            statement.executeUpdate("CREATE DATABASE `" + this.database + "` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
        } catch (SQLException e) {
            System.out.println("Could not create database: " + e.getLocalizedMessage());
        }
    }

    public boolean exists() {
        PreparedStatement statement = statementFactory.prepare("SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = ?");
        try {
            statement.setString(1, this.database);
            ResultSet result = statement.executeQuery();
            return result.next();
        } catch (SQLException e) {
            System.out.println("Error during checking if database exists: " + e.getLocalizedMessage());
        }

        return false;
    }
}
