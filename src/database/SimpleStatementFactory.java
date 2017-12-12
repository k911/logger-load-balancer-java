package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class SimpleStatementFactory implements StatementFactory {

    private Connection connection;

    public SimpleStatementFactory(Connection connection) {
        this.connection = connection;
    }


    @Override
    public Statement make() {
        try {
            return connection.createStatement();
        } catch (SQLException e) {
            System.out.println("Could not create statement: " + e.getLocalizedMessage());
        }

        return null;
    }

    @Override
    public PreparedStatement prepare(String sql) {
        try {
            return connection.prepareStatement(sql);
        } catch (SQLException e) {
            System.out.println("Could not create statement: " + e.getLocalizedMessage() + ". SQL Query: " + sql);
        }

        return null;
    }
}
