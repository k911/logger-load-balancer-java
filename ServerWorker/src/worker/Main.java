package worker;

import worker.server.WorkerServer;
import worker.server.config.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Main {

    private final static Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {

        WorkerServerConfiguration serverConfiguration = prepareConfig();

        logger.info("Starting the WorkServer " + serverConfiguration.getName());

        WorkerServer workerServer = new WorkerServer(serverConfiguration);

        workerServer.run();

    }

    public static WorkerServerConfiguration prepareConfig() {

        WorkerServerConfiguration serverConfiguration = new WorkerServerConfiguration();
        serverConfiguration.setName("WorkerServer-1");
        try {
            serverConfiguration.setInetAddress(InetAddress.getLocalHost());
        } catch (UnknownHostException e) {
            logger.severe("Configuration setup failed. Could not resolve InetAddress "
                    + e.getMessage());
        }
        serverConfiguration.setPort(8082);
        serverConfiguration.setServerThreadPoolSize(30);
        List<WorkerConfiguration> workerConfigurations;
        workerConfigurations = new ArrayList<WorkerConfiguration>();

        workerConfigurations.add(new WorkerConfigurationBuilder().name("Worker-1").buildWorkerConfiguration());

        serverConfiguration.setWorkerConfigurations(workerConfigurations);

        DaoConfiguration daoConfiguration = prepareDaoConfig();
        DBConnectionConfiguration dbConnectionConfiguration = prepareDBConnectionConfig();

        serverConfiguration.setDaoConfiguration(daoConfiguration);
        serverConfiguration.setDbConnectionConfiguration(dbConnectionConfiguration);

        return serverConfiguration;
    }

    private static DBConnectionConfiguration prepareDBConnectionConfig() {

        DBConnectionConfiguration dbConnectionConfiguration = new DBConnectionConfiguration();
        dbConnectionConfiguration.setDriverName("com.mysql.jdbc.Driver");
        dbConnectionConfiguration.setDatabaseSpecificAddress("jdbc:mysql://");
        dbConnectionConfiguration.setDatabaseServerAddress("127.0.0.1");
        dbConnectionConfiguration.setPort(3306);
        dbConnectionConfiguration.setUserName("quiz_account");
        dbConnectionConfiguration.setPassword("quiz_account");

        return dbConnectionConfiguration;

    }


    private static DaoConfiguration prepareDaoConfig() {
        DaoConfiguration daoConfiguration = new DaoConfiguration();

        List<String> sqlCreationList = new ArrayList<>();

        sqlCreationList.add(
                "CREATE TABLE `answers` (\n" +
                        "  `quiz_id` int(11) NOT NULL,\n" +
                        "  `id` int(11) NOT NULL,\n" +
                        "  `question_id` int(11) NOT NULL,\n" +
                        "  `text` varchar(512) NOT NULL\n" +
                        ") "
        );
        sqlCreationList.add(
                "INSERT INTO `answers` (`quiz_id`, `id`, `question_id`, `text`) VALUES\n" +
                        "(1001, 1, 1, 'AnswerIcorrect'),\n" +
                        "(1001, 1, 2, 'AnswerIcorrect'),\n" +
                        "(1001, 1, 3, 'AnswerIcorrect'),\n" +
                        "(1001, 2, 1, 'AnswerIcorrect'),\n" +
                        "(1001, 2, 2, 'Correct'),\n" +
                        "(1001, 2, 3, 'Correct'),\n" +
                        "(1001, 3, 1, 'AnswerIcorrect'),\n" +
                        "(1001, 3, 2, 'Correct'),\n" +
                        "(1001, 4, 1, 'Correct'),\n" +
                        "(1002, 1, 1, 'AnswerIcorrect'),\n" +
                        "(1002, 1, 2, 'AnswerIcorrect'),\n" +
                        "(1002, 2, 1, 'AnswerIcorrect'),\n" +
                        "(1002, 2, 2, 'Correct'),\n" +
                        "(1002, 3, 1, 'AnswerIcorrect'),\n" +
                        "(1002, 3, 2, 'Correct'),\n" +
                        "(1002, 4, 1, 'Correct'),\n" +
                        "(1003, 1, 1, 'AnswerIcorrect'),\n" +
                        "(1003, 2, 1, 'Correct');"
        );
        sqlCreationList.add(
                "CREATE TABLE `correct_answers` (\n" +
                        "  `question_id` int(11) NOT NULL,\n" +
                        "  `quiz_id` int(11) NOT NULL,\n" +
                        "  `answer_id` int(11) NOT NULL\n" +
                        ")"
        );
        sqlCreationList.add("INSERT INTO `correct_answers` (`question_id`, `quiz_id`, `answer_id`) VALUES\n" +
                "(1, 1001, 4),\n" +
                "(1, 1002, 4),\n" +
                "(1, 1003, 2),\n" +
                "(2, 1001, 2),\n" +
                "(2, 1001, 3),\n" +
                "(2, 1002, 2),\n" +
                "(2, 1002, 3),\n" +
                "(3, 1001, 2);");
        sqlCreationList.add(
                "CREATE TABLE `question` (\n" +
                        "  `quiz_id` int(11) NOT NULL,\n" +
                        "  `id` int(11) NOT NULL,\n" +
                        "  `text` varchar(512) NOT NULL,\n" +
                        "  `value` int(11) NOT NULL DEFAULT '1'\n" +
                        ") "
        );
        sqlCreationList.add("INSERT INTO `question` (`quiz_id`, `id`, `text`, `value`) VALUES\n" +
                "(1001, 1, 'QuestionOneOfOne', 1),\n" +
                "(1001, 2, 'QuestionTwoOfOne', 1),\n" +
                "(1001, 3, 'QuestionThreeOfOne', 1),\n" +
                "(1002, 1, 'QuestionOneOfTwo', 1),\n" +
                "(1002, 2, 'QuestionTwoOfTwo', 1),\n" +
                "(1003, 1, 'QuestionOneOfThree', 1);");
        sqlCreationList.add(
                "CREATE TABLE `quiz` (\n" +
                        "  `id` int(11) NOT NULL,\n" +
                        "  `active` tinyint(1) NOT NULL DEFAULT '1'\n" +
                        ");"
        );
        sqlCreationList.add("INSERT INTO `quiz` (`id`, `active`) VALUES\n" +
                "(1001, 1),\n" +
                "(1002, 1),\n" +
                "(1003, 1);");

        sqlCreationList.add(
                "CREATE TABLE `results` (\n" +
                        "  `quiz_id` int(11) NOT NULL,\n" +
                        "  `question_id` int(11) NOT NULL,\n" +
                        "  `NIU` varchar(16) NOT NULL,\n" +
                        "  `answer_id` int(11) NOT NULL,\n" +
                        "  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP\n" +
                        ") "
        );

        sqlCreationList.add("INSERT INTO `results` (`quiz_id`, `question_id`, `NIU`, `answer_id`, `timestamp`) " +
                "VALUES\n" +
                "(1001, 1, 'NIU-1', 1, '2018-01-10 01:28:02'),\n" +
                "(1001, 1, 'NIU-2', 1, '2018-01-10 01:28:02'),\n" +
                "(1001, 2, 'NIU-1', 2, '2018-01-10 01:28:02'),\n" +
                "(1001, 2, 'NIU-1', 3, '2018-01-10 01:28:02'),\n" +
                "(1001, 2, 'NIU-2', 2, '2018-01-10 01:28:02'),\n" +
                "(1001, 2, 'NIU-2', 3, '2018-01-10 01:28:02'),\n" +
                "(1001, 3, 'NIU-1', 2, '2018-01-10 01:28:02'),\n" +
                "(1001, 3, 'NIU-2', 1, '2018-01-10 01:28:02');");

        sqlCreationList.add("ALTER TABLE `answers`\n" +
                "  ADD UNIQUE KEY `quiz_id` (`quiz_id`,`id`,`question_id`),\n" +
                "  ADD KEY `quiz_id_2` (`quiz_id`,`question_id`);");
        sqlCreationList.add("ALTER TABLE `correct_answers`\n" +
                "  ADD UNIQUE KEY `question_id` (`question_id`,`quiz_id`,`answer_id`),\n" +
                "  ADD KEY `quiz_id` (`quiz_id`,`question_id`,`answer_id`);");
        sqlCreationList.add("ALTER TABLE `question`\n" +
                "  ADD PRIMARY KEY (`quiz_id`,`id`);");
        sqlCreationList.add("ALTER TABLE `quiz`\n" +
                "  ADD PRIMARY KEY (`id`);");
        sqlCreationList.add("ALTER TABLE `results`\n" +
                "  ADD UNIQUE KEY `quiz_id` (`quiz_id`,`question_id`,`NIU`,`answer_id`),\n" +
                "  ADD KEY `quiz_id_2` (`quiz_id`,`question_id`,`answer_id`);");

        sqlCreationList
                .add("ALTER TABLE `answers`\n" +
                        "  ADD CONSTRAINT `answers_ibfk_1` FOREIGN KEY (`quiz_id`,`question_id`) REFERENCES " +
                        "`question` (`quiz_id`, `id`) ON DELETE NO ACTION ON UPDATE NO ACTION;\n");
        sqlCreationList
                .add("ALTER TABLE `correct_answers`\n" +
                        "  ADD CONSTRAINT `correct_answers_ibfk_1` FOREIGN KEY (`quiz_id`,`question_id`,`answer_id`) " +
                        "REFERENCES `answers` (`quiz_id`, `question_id`, `id`);");
        sqlCreationList
                .add("ALTER TABLE `question`\n" +
                        "  ADD CONSTRAINT `question_ibfk_1` FOREIGN KEY (`quiz_id`) REFERENCES `quiz` (`id`) ON " +
                        "DELETE CASCADE ON UPDATE CASCADE;");
        sqlCreationList
                .add("ALTER TABLE `results`\n" +
                        "  ADD CONSTRAINT `results_ibfk_1` FOREIGN KEY (`quiz_id`,`question_id`,`answer_id`) " +
                        "REFERENCES `answers` (`quiz_id`, `question_id`, `id`);");

        daoConfiguration.setUsedSchema("testquiz123");
        daoConfiguration.setCreateStatements(sqlCreationList);

        return daoConfiguration;
    }
}
