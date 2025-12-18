package org.football;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
        String JDBC_URL = "jdbc:postgresql://localhost:5432/mini_football_db";
        String USERNAME = "mini_football_db_manager";
        String PASSWORD = "football";

        public Connection getDBConnection() throws SQLException {
            System.out.println("Connecting to database...");
            return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);

    }
}
