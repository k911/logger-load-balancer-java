package database;

import java.sql.PreparedStatement;
import java.sql.Statement;

public interface StatementFactory {
    Statement make();
    PreparedStatement prepare(String sql);
}
