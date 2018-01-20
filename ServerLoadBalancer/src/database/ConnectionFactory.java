package database;

import java.sql.Connection;

public interface ConnectionFactory {
    Connection make();
}
