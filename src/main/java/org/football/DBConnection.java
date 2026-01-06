package org.football;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
        String JDBC_URL = System.getenv("URL");
        String USERNAME = System.getenv("USER");
        String PASSWORD = System.getenv("PASSWORD");

        public Connection getDBConnection() throws SQLException {
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException("Postgres Driver not found",e);
            }
            return DriverManager.getConnection(JDBC_URL,USERNAME,PASSWORD);
    }
}
