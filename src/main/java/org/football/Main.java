package org.football;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException {
        DBConnection dbc = new DBConnection();
        DataRetriever dr = new DataRetriever();


        List<Player> newPlayers = new ArrayList<>();
        Team team = new Team(6, "ASIATIC", ContinentEnum.ASIA);
        Player player1 = new Player(5, "Nomena", 19, PlayerPositionEnum.GK, team);
        Player player2 = new Player(6, "Lahatra", 20, PlayerPositionEnum.DEF, team   );

        try {
            List<Player> inserted = dr.createPlayers(newPlayers);

            System.out.println("Number of player inserted : " + inserted.size());
            for (Player p : inserted) {
                System.out.println("Days created : " + p.getName());
            }

        } catch (RuntimeException e) {
            System.out.println("Error during insertion : " + e.getMessage());
        }


    }
}
