package repository;

import database.MysqlDatabaseManager;
import items.HttpException;
import items.Log;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class LogRepository {
    private MysqlDatabaseManager databaseManager;

    public LogRepository(MysqlDatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void add(Log log) throws HttpException {
        PreparedStatement statement = databaseManager.prepareStatement("INSERT INTO logs (message, context, created_at) VALUES (?, ?, ?)");
        try {
            statement.setString(1, log.getMessage());
            statement.setString(2, log.getContext());
            statement.setTimestamp(3, log.getCreatedAt());
            statement.executeUpdate();

            ResultSet results = statement.getGeneratedKeys();
            if (results.next()) {
                log.setId(results.getInt(1));
            }
        } catch (SQLException e) {
            throw new HttpException("Could not add log to database: " + e.getLocalizedMessage(), 500);
        }
    }

    public int count() {
        try {
            ResultSet results = databaseManager.executeQuery("SELECT COUNT(DISTINCT id) as logsCount FROM logs;");
            if (results.next()) {
                return results.getInt("logsCount");
            }
        } catch (SQLException e) {
            System.err.println("Cannot count logs");
        }
        return 0;
    }

    public Collection<Log> findAll(int offset, int limit) throws HttpException {
        Collection<Log> logs = new ArrayList<>();

        PreparedStatement statement = databaseManager.prepareStatement("SELECT * FROM logs LIMIT ? OFFSET ? ORDER BY id ASC;");
        ResultSet results;
        try {
            statement.setInt(1, limit);
            statement.setInt(2, offset);
            results = statement.executeQuery();
            while (results.next()) {
                logs.add(new Log(results.getInt("id"), results.getString("message"), results.getString("context"), results.getTimestamp("created_at")));
            }
        } catch (SQLException e) {
            throw new HttpException("Error getting logs from database: " + e.getLocalizedMessage(), 400);
        }

        return logs;
    }
}
