package repository;

import database.MysqlDatabaseManager;
import items.HttpException;
import items.Log;
import items.Worker;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class WorkerRepository {
    private MysqlDatabaseManager databaseManager;

    public WorkerRepository(MysqlDatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void add(Worker worker) throws HttpException {
        PreparedStatement statement = databaseManager.prepareStatement("INSERT INTO workers (host, port, tasks) VALUES (?, ?, ?)");
        try {
            statement.setString(1, worker.getHost());
            statement.setInt(2, worker.getPort());
            statement.setInt(3, worker.getTaskCount());
            statement.executeUpdate();

            ResultSet results = statement.getGeneratedKeys();
            if(results.next()) {
                worker.setId(results.getInt(1));
            }
        } catch (SQLException e) {
            throw new HttpException("Error while adding to database: " + e.getLocalizedMessage(), 400);
        }
    }

    public void update(Worker worker) throws HttpException {
        PreparedStatement statement = databaseManager.prepareStatement("UPDATE workers SET host = ?, port = ?, tasks = ? WHERE id = ?");
        try {
            statement.setString(1, worker.getHost());
            statement.setInt(2, worker.getPort());
            statement.setInt(3, worker.getTaskCount());
            statement.setInt(4, worker.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new HttpException("Error while updating: " + e.getLocalizedMessage(), 400);
        }
    }

    public Worker first() throws HttpException {
        ResultSet results = databaseManager.executeQuery("SELECT * FROM workers ORDER BY tasks ASC LIMIT 1;");
        try {
            if (results.next()) {
                return new Worker(results.getInt("id"), results.getInt("tasks"), results.getInt("port"), results.getString("host"));
            }
        } catch (SQLException e) {
            throw new HttpException("No workers set yet.", 404);
        }

        return null;
    }

    public void truncate() {
        databaseManager.executeUpdate("TRUNCATE workers");
    }

    public Collection<Worker> findAll() {
        Collection<Worker> workers = new ArrayList<>();

        ResultSet results = databaseManager.executeQuery("SELECT * FROM workers;");
        try {
            while (results.next()) {
                workers.add(new Worker(results.getInt("id"), results.getInt("tasks"), results.getInt("port"), results.getString("host")));
            }
        } catch (SQLException e) {
            System.out.println("Error getting logs from database: " + e.getLocalizedMessage());
        }

        return workers;
    }
}
