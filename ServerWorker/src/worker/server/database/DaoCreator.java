package ServerWorker.src.worker.server.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DaoCreator extends DaoBase {

    private final static Logger logger = Logger.getLogger(DaoCreator.class.getName());
    private List<String> sqlCreateStatements;


    public DaoCreator() {
        System.out.println("DaoCreator() ctor");
        sqlCreateStatements = new ArrayList<>();
    }

    public DaoCreator(Connection dbConnection, String usedSchema, List<String> sqlCreateStatements) {
        this.setDbConnection(dbConnection);
        this.setUsedSchema(usedSchema);
        this.sqlCreateStatements = sqlCreateStatements;

    }


    @Override
    public boolean initialize() {
        String schemaName = getUsedSchema();

        if (getDbConnection() == null || schemaName == null) {
            logger.warning("initialization was no completed!");
            return false;
        }
        boolean isSchemaValid = true;
        Statement st = createStatement(getDbConnection());
        if (!selectSchema(schemaName, st)) {
            if (createSchema(schemaName, st)) {
                logger.info("Schema " + schemaName + " has been created");

            } else {
                logger.info("Unable to create schema");
                isSchemaValid = false;
            }
            //#TODO SQL agnostic check for table existence
            if (createTables(sqlCreateStatements, st))
                logger.info("Tables created");
            else {
                logger.warning("Tables were not created!");
                isSchemaValid = false;
            }

            if (isSchemaValid) {
                logger.info("Schema validation is completed");

            } else {
                logger.severe("Schema validation is not completed!");
                return false;
            }
        } else
            logger.info("Schema is selected");


        return true;
    }


    private boolean createSchema(String usedSchema, Statement st) {
        return executeUpdate(st, "create Database " + usedSchema + ";") == 1;
    }

    private boolean createTables(List<String> sqlStatements, Statement st) {
        try {

            selectSchema(this.getUsedSchema(), st);
            this.getDbConnection().setAutoCommit(false);


            for (String sql : sqlStatements) {
                System.out.println("executing: " + sql);
                st.executeUpdate(sql);
            }

            this.getDbConnection().commit();
            this.getDbConnection().setAutoCommit(true);
            System.out.println("commiting");

        } catch (SQLException e) {
            logger.severe("Could not create tables : \nState: " + e.getSQLState() + " Message: " + e.getMessage());
            return false;
        }


        return true;
    }

    public List<String> getSqlCreateStatements() {
        return sqlCreateStatements;
    }

    public void setSqlCreateStatements(List<String> sqlCreateStatements) {
        this.sqlCreateStatements = sqlCreateStatements;
    }
}
