package ServerWorker.src.worker.server.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public abstract class DaoBase {

    private final static Logger logger = Logger.getLogger(DaoBase.class.getName());
    private String usedSchema;
    private Connection dbConnection;

    protected abstract boolean initialize();


    public String getUsedSchema() {
        return usedSchema;
    }

    public void setUsedSchema(String usedSchema) {
        this.usedSchema = usedSchema;
    }

    public Connection getDbConnection() {
        return dbConnection;
    }

    public void setDbConnection(Connection dbConnection) {
        this.dbConnection = dbConnection;
    }

    protected int executeUpdate(Statement s, String sql) {
        try {
            return s.executeUpdate(sql);
        } catch (SQLException e) {
            logger.warning("sql statement: " + sql + " could not be executed " + e.getMessage() + " Error Code: " +
                    e.getErrorCode());
            return -1;
        }

    }

    protected Statement createStatement(Connection connection) {
        try {
            return connection.createStatement();
        } catch (SQLException e) {
            logger.warning(
                    "Exception thrown when creating Statement " + e.getMessage() + " Error Code: " + e.getErrorCode());
            return null;
        }

    }

    protected boolean selectSchema(String usedSchema, Statement st) {
        return executeUpdate(st, "USE " + usedSchema + ";") == 0;
    }


}
