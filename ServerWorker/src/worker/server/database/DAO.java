package ServerWorker.src.worker.server.database;

import ServerWorker.src.worker.server.config.DaoConfiguration;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DAO extends DaoBase {
    private final static Logger logger = Logger.getLogger(DAO.class.getName());
    private DaoCreator daoCreator;
    private DaoConfiguration configuration;
    private Connection dbConnection;
    private Statement st;


    public DAO(DaoConfiguration configuration, Connection connection) {
        System.out.println("DAO Ctor()");
        this.dbConnection = connection;

        if (processConfiguration(configuration))
            logger.info("Loaded configation for DAO");
        else {
            logger.warning("DAO was unable to load config - using default settings");
            loadDefaultConfig();
        }

    }

    @Override
    public boolean initialize() {
        System.out.println("DAO initialize()");

        if (dbConnection == null) {
            logger.warning("initialization was no completed!");
            return false;
        } else {
            daoCreator = new DaoCreator();
            daoCreator.setDbConnection(dbConnection);
            daoCreator.setUsedSchema(configuration.getUsedSchema());
            daoCreator.setSqlCreateStatements(configuration.getCreateStatements());
            System.out.println("daoCreator.initialize()");
            if (daoCreator.initialize())
                logger.info("daoCreator initialized");
            else {
                logger.warning("daoCreator failed to initialize");
                return false;
            }
        }
        st = createStatement(dbConnection);
        if (st == null) {
            logger.warning("unable to create Statement object during initialization");
            return false;
        }

        return true;
    }


    public boolean loadConfiguartion(DaoConfiguration configuration) {
        return processConfiguration(configuration);
    }

    private boolean processConfiguration(DaoConfiguration configuration) {

        if (configuration == null) {
            return false;
        }

        this.configuration = configuration;
        return true;
    }


    private void loadDefaultConfig() {
        if (configuration == null)
            configuration = new DaoConfiguration();
        configuration.setUsedSchema("quiztest123");
        List<String> sqlCreationList = new ArrayList<>();

        sqlCreationList
                .add("CREATE TABLE `answers` (`question_id` int(11) NOT NULL,`id` int(11) NOT NULL,`text` varchar" +
                        "(512) NOT NULL);");
        sqlCreationList
                .add("CREATE TABLE `correct_answers` (`question_id` int(11) NOT NULL,`answer_id` int(11) NOT NULL);");
        sqlCreationList
                .add("CREATE TABLE `question` (`quiz_id` int(11) NOT NULL,`id` int(11) NOT NULL,`text` varchar(512) " +
                        "NOT NULL,`value` int(11) NOT NULL DEFAULT '1');");
        sqlCreationList.add("CREATE TABLE `quiz` (`id` int(11) NOT NULL,`active` tinyint(1) NOT NULL DEFAULT '1');");
        sqlCreationList
                .add("CREATE TABLE `results` (`id` int(11) NOT NULL,`quiz_id` int(11) NOT NULL,`NIU` varchar(16) NOT " +
                        "NULL,`score` int(11) NOT NULL,  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP);");
        sqlCreationList.add("ALTER TABLE `answers` ADD PRIMARY KEY (`question_id`,`id`);");
        sqlCreationList
                .add("ALTER TABLE `correct_answers` ADD UNIQUE KEY `question_id_2` (`question_id`), ADD KEY " +
                        "`question_id` (`question_id`,`answer_id`);");
        sqlCreationList.add("ALTER TABLE `question` ADD PRIMARY KEY (`quiz_id`,`id`);");
        sqlCreationList.add("ALTER TABLE `quiz` ADD PRIMARY KEY (`id`);");
        sqlCreationList.add("ALTER TABLE `results` ADD KEY `quiz_id` (`quiz_id`);");
        sqlCreationList
                .add("ALTER TABLE `answers` ADD CONSTRAINT `answers_ibfk_1` FOREIGN KEY (`question_id`) REFERENCES " +
                        "`question` (`quiz_id`) ON DELETE CASCADE ON UPDATE CASCADE;");
        sqlCreationList
                .add("ALTER TABLE `correct_answers` ADD CONSTRAINT `correct_answers_ibfk_1` FOREIGN KEY " +
                        "(`question_id`,`answer_id`) REFERENCES `answers` (`question_id`, `id`);");
        sqlCreationList
                .add("ALTER TABLE `question` ADD CONSTRAINT `question_ibfk_1` FOREIGN KEY (`quiz_id`) REFERENCES " +
                        "`quiz` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;");
        sqlCreationList
                .add("ALTER TABLE `results` ADD CONSTRAINT `results_ibfk_1` FOREIGN KEY (`quiz_id`) REFERENCES `quiz`" +
                        " (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;");

        configuration.setCreateStatements(sqlCreationList);


    }

    private boolean checkDriver(String driver) {
        try {
            Class.forName(driver).newInstance();
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}
