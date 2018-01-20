package server;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

class FutureTaskCallback<T> extends FutureTask<T> {
    private Connection connection;

    FutureTaskCallback(Callable<T> callable, Connection connection) {
        super(callable);
        this.connection = connection;
    }

    public void done() {

        try {
            connection.close();
        } catch (SQLException e) {
            System.err.println("Cannot close MySQL connection");
        }

        String msg = "Result: ";
        if (isCancelled()) {
            msg += "Cancelled.";
        } else {
            try {
                msg += get();
            } catch (Exception exc) {
                msg += exc.toString();
            }
        }
        System.out.println("\n" + msg + "\n");
    }
}