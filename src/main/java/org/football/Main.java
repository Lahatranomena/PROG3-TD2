package org.football;


import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        DBConnection dbc = new DBConnection();
        DataRetriever dr = new DataRetriever();

        System.out.println(dr.findTeamById(1));
    }
}