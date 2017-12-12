package database;

import java.sql.Connection;

public interface ConnectionFactory {
    Connection make(String uri, String username, String password);
}
