package repository;

import database.MysqlDatabaseManager;
import items.Log;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class LogRepository {
    private MysqlDatabaseManager databaseManager;

    public LogRepository(MysqlDatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void add(Log log) {
        PreparedStatement statement = databaseManager.prepareStatement("INSERT INTO logs (message, context, created_at) VALUES (?, ?, ?)");
        try {
            statement.setString(1, log.getMessage());
            statement.setString(2, log.getContext());
            statement.setTimestamp(3, log.getCreatedAt());
            statement.executeUpdate();

            ResultSet results = statement.getGeneratedKeys();
            if(results.next()) {
                log.setId(results.getInt(1));
            }
        } catch (SQLException e) {
            System.out.println("Error saving log into database: " + e.getLocalizedMessage());
        }
    }

    public Collection<Log> findAll(int offset, int limit) {
        Collection<Log> logs = new ArrayList<>();

        PreparedStatement statement = databaseManager.prepareStatement("SELECT * FROM logs LIMIT ? OFFSET ?;");
        ResultSet results;
        try {
            statement.setInt(1, limit);
            statement.setInt(2, offset);
            results = statement.executeQuery();
            while (results.next()) {
                logs.add(new Log(results.getInt("id"), results.getString("message"), results.getString("context"), results.getTimestamp("created_at")));
            }
        } catch (SQLException e) {
            System.out.println("Error getting logs from database: " + e.getLocalizedMessage());
            return logs;
        }

        return logs;
    }
}
