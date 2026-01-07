package org.football;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException {
        DBConnection dbc = new DBConnection();
        DataRetriever dr = new DataRetriever();
//
        System.out.println(dr.findTeamById(1));
//
//        System.out.println(dr.findTeamById(5));
//
//        System.out.println(dr.findPlayers(1, 2));
//
//        System.out.println(dr.findPlayers(3, 5));
//
//        System.out.println(dr.findTeamsByPlayerName("an"));
//
//        System.out.println(dr.findPlayersByCriteria("ud",
//                PlayerPositionEnum.MIDF, "Madrid", ContinentEnum.EUROPA, 1, 10));

//        System.out.println(
//                dr.createPlayers(
//                        List.of(
//                                new Player(6, "Jude Bellingham", 23, PlayerPositionEnum.STR, null),
//                                new Player(7, "Pedri", 24, PlayerPositionEnum.MIDF, null)
//                        )
//                )
//        );
//
//        List<Player> result = dr.createPlayers(
//                List.of(
//                        new Player(6, "Vini", 25, PlayerPositionEnum.STR, null),
//                        new Player(7, "Pedri", 24, PlayerPositionEnum.MIDF, null)
//             )
//        );
//        Team team = new Team();
//        team.setId(2);
//        Player newPlayer = new Player(6, "Vini", 25, PlayerPositionEnum.STR, null);
//        System.out.println(team.getPlayers().add(newPlayer));


//        Team savedTeam = new DataRetriever().saveTeam(team);
//
//        Team team = new Team();
//        team.setId(2);
//        team.setPlayers(new ArrayList<>());
//
//        Team savedTeam = new DataRetriever().saveTeam(team);
//
//        System.out.println("Update successfully");
//        for (Player p : savedTeam.getPlayers()) {
//            System.out.println(p.getId() + " - " + p.getName());
//        }
//
//        List<Player> result = dr.findPlayersByCriteria(
//                "ud",
//                PlayerPositionEnum.MIDF,
//                "Madrid",
//                ContinentEnum.EUROPA,
//                1,
//                10
//        );

//        for (Player p : result) {
//            System.out.println(p.getName() + " - " + p.getPosition() + " - " + p.getTeam().getName());
//        }


    }
}
