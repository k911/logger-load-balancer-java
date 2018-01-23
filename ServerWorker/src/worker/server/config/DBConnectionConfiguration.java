package ServerWorker.src.worker.server.config;

public class DBConnectionConfiguration {

    private String driverName;
    private String databaseSpecificAddress;
    private String databaseServerAddress;
    private Integer port;
    private String userName;
    private String password;

    public DBConnectionConfiguration() {
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDatabaseSpecificAddress() {
        return databaseSpecificAddress;
    }

    public void setDatabaseSpecificAddress(String databaseSpecificAddress) {
        this.databaseSpecificAddress = databaseSpecificAddress;
    }

    public String getDatabaseServerAddress() {
        return databaseServerAddress;
    }

    public void setDatabaseServerAddress(String databaseServerAddress) {
        this.databaseServerAddress = databaseServerAddress;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
