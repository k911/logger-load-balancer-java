package repository;

import database.MysqlDatabaseManager;

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

    public void saveLog(String message) {
        PreparedStatement statement = databaseManager.prepareStatement("INSERT INTO logs (message, attributes) VALUES (?, ?)");
        try {
            statement.setString(1, message);
            statement.setString(2, "test");
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error saving log into database: " + e.getLocalizedMessage());
        }
    }

    public Collection<Map<String, String>> getLogs(int offset, int limit) {

        Collection<Map<String, String>> logs = new ArrayList<>();

        PreparedStatement statement = databaseManager.prepareStatement("SELECT * FROM logs LIMIT ? OFFSET ?;");
        ResultSet results;
        try {
            statement.setInt(1, limit);
            statement.setInt(2, offset);
            results = statement.executeQuery();
            while (results.next()) {
                Map<String, String> log = new HashMap<>();
                log.put("id", String.valueOf(results.getInt("id")));
                log.put("message", results.getString("message"));
                log.put("attributes", results.getString("attributes"));
                log.put("created_at", results.getTimestamp("created_at").toString());
                logs.add(log);
            }
        } catch (SQLException e) {
            System.out.println("Error getting logs from database: " + e.getLocalizedMessage());
            return logs;
        }

        return logs;
    }
}
