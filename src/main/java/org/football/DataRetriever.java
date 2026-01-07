package org.football;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataRetriever {

    DBConnection connection = new DBConnection();

    Team findTeamById(Integer id) {
        List<Player> players = new ArrayList<Player>();
        String sql = """
                SELECT
                    team.id AS team_id, team.name AS team_name, team.continent, player.id AS player_id,
                    player.name AS player_name, player.age AS age, player.position AS positions, 
                    player.goal_nb FROM team\s
                    LEFT JOIN player ON team.id = player.id_team
                WHERE team.id = ?;
               \s
               \s""";

        try (PreparedStatement statement = connection.getDBConnection().prepareStatement(sql)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                Team team = null;
                while (resultSet.next()) {
                    team = new Team(
                            resultSet.getInt("team_id"),
                            resultSet.getString("team_name"),
                            ContinentEnum.valueOf(resultSet.getString("continent"))
                    );
                    team.setPlayers(players);
                    {
                        Player player = new Player();
                        player.setId(resultSet.getInt("player_id"));
                        player.setName(resultSet.getString("player_name"));
                        player.setAge(resultSet.getInt("age"));
                        player.setGoalNb(resultSet.getInt("goal_nb"));
                        String pos = resultSet.getString("positions");

                        if (pos != null) {
                            player.setPosition(PlayerPositionEnum.valueOf(pos));
                        } else {
                            player.setPosition(null);
                        }


                        players.add(player);
                    }
                }
                return team;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    List<Player> findPlayers(int page, int size) {
        List<Player> players = new ArrayList<>();
        int offset = (page - 1) * size;

        String sql = """
                SELECT player.id AS player_id, player.name AS player_name, player.age AS
                	age, player.position AS positions, player.id_team AS team 
                player.goal_nb FROM
                	player LIMIT ? OFFSET ?;
                """;

        try (PreparedStatement statement = connection.getDBConnection().prepareStatement(sql)) {
            statement.setInt(1, size);
            statement.setInt(2, offset);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Player player = new Player();
                    player.setId(resultSet.getInt("player_id"));
                    player.setName(resultSet.getString("player_name"));
                    player.setAge(resultSet.getInt("age"));
                    player.setGoalNb(resultSet.getInt("goal_nb"));
                    player.setPosition(
                            PlayerPositionEnum.valueOf(resultSet.getString("positions"))
                    );
                    players.add(player);
                }
            }
            return players;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    List<Player> createPlayers(List<Player> newPlayers) {

        String insertQuery = """
                   INSERT INTO player(id, name, age, position, goal_nb)
                       VALUES (?, ?, ?, ?::position_enum, ?)
               \s""";

        try (Connection conn = connection.getDBConnection()) {
            conn.setAutoCommit(false);
            Set<String> names = new HashSet<>();
            for (Player player : newPlayers) {
                if (!names.add(player.getName())) {
                    throw new RuntimeException(
                            "Player " + player.getName() + " is duplicated in the list."
                    );
                }
            }
            try (PreparedStatement statement = conn.prepareStatement(insertQuery)) {
                for (Player player : newPlayers) {
                    statement.setInt(1, player.getId());
                    statement.setString(2, player.getName());
                    statement.setInt(3, player.getAge());
                    statement.setObject(4, player.getPosition(), java.sql.Types.OTHER);
                    statement.setObject(5, player.getGoalNb());
                    statement.executeUpdate();
                }
            }
            conn.commit();
            System.out.println("Success: " + newPlayers);
            return newPlayers;
        } catch (Exception e) {
            throw new RuntimeException("Transaction cancel : ", e);
        }
    }

    Team saveTeam(Team teamToSave) throws SQLException {
        try (Connection conn = connection.getDBConnection()) {
            conn.setAutoCommit(false);

            String updateSQL = "UPDATE player SET id_team = NULL WHERE id_team = ?";
            try (PreparedStatement psUpdate = conn.prepareStatement(updateSQL)) {
                psUpdate.setInt(1, teamToSave.getId());
                psUpdate.executeUpdate();
            }

            String insertSQL = """
            INSERT INTO player(id, name, age, position, id_team, goal_nb)
            VALUES (?, ?, ?, ?::position_enum, ?)
            ON CONFLICT (id)
            DO UPDATE SET name = EXCLUDED.name, age = EXCLUDED.age,
                          position = EXCLUDED.position, id_team = EXCLUDED.id_team
        """;

            try (PreparedStatement psInsert = conn.prepareStatement(insertSQL)) {
                for (Player player : teamToSave.getPlayers()) {
                    psInsert.setInt(1, player.getId());
                    psInsert.setString(2, player.getName());
                    psInsert.setInt(3, player.getAge());
                    psInsert.setString(4, player.getPosition().name());
                    psInsert.setInt(5, player.getGoalNb());
                    psInsert.setInt(6, teamToSave.getId());
                    psInsert.executeUpdate();
                }
            }

            conn.commit();
            return teamToSave;

        } catch (SQLException e) {
            throw new RuntimeException("Transaction cancel : ", e);
        }
    }



    List<Team> findTeamsByPlayerName(String playerName) throws SQLException {

        List<Team> teams = new ArrayList<>();
        Player player = new Player();

        StringBuilder findTeamsByPlayerNameQuery = new StringBuilder("""
                SELECT team.id AS team_id, team.name AS team_name, team.continent AS continent, 
                player.name AS player_name FROM team LEFT JOIN player ON player.id_team = team.id
                WHERE 1 = 1
                """);

        List<Object> parameters = new ArrayList<>();

        if (playerName != null) {
            findTeamsByPlayerNameQuery.append("and player.name ilike ? ");
            parameters.add("%" + playerName + "%");
        }
        try (Connection conn = connection.getDBConnection();
             PreparedStatement statement = conn.prepareStatement(findTeamsByPlayerNameQuery.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                if (rs.getInt("team_id") != 0) {
                    Team team = new Team();
                    team.setId(rs.getInt("team_id"));
                    team.setName(rs.getString("team_name"));
                    team.setContinent(ContinentEnum.valueOf(rs.getString("continent")));
                    teams.add(team);
                }
            }

        }
        return teams;
    }


    public List<Player> findPlayersByCriteria(
            String playerName,
            PlayerPositionEnum position,
            String teamName,
            ContinentEnum continent,
            int page,
            int size
    ) throws SQLException {
        List<Player> players = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT player.id AS player_id, player.name AS player_name, player.age AS age, " +
                        "player.position AS position, player.goal_nb, team.name AS team, team.continent AS continent " +
                        "FROM player " +
                        "LEFT JOIN team ON player.id_team = team.id " +
                        "WHERE 1=1 "
        );

        List<Object> parameters = new ArrayList<>();

        if (playerName != null) {
            sql.append("AND player.name ILIKE ? ");
            parameters.add("%" + playerName + "%");
        }
        if (position != null) {
            sql.append("AND player.position = ?::position_enum ");
            parameters.add(position.toString());
        }
        if (teamName != null) {
            sql.append("AND team.name ILIKE ? ");
            parameters.add("%" + teamName + "%");
        }
        if (continent != null) {
            sql.append("AND team.continent = ?::continent_enum ");
            parameters.add(continent.toString());
        }


        sql.append("LIMIT ? OFFSET ?");
        int offset = (page - 1) * size;
        parameters.add(size);
        parameters.add(offset);

        try (Connection conn = connection.getDBConnection();
             PreparedStatement statement = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                Object param = parameters.get(i);
                if (param instanceof Integer) {
                    statement.setInt(i + 1, (Integer) param);
                } else {
                    statement.setString(i + 1, param.toString());
                }
            }

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Player player = new Player();
                player.setId(rs.getInt("player_id"));
                player.setName(rs.getString("player_name"));
                player.setAge(rs.getInt("age"));
                player.setPosition(PlayerPositionEnum.valueOf(rs.getString("position")));
                player.setGoalNb(rs.getInt("goal_nb"));

                Team team = new Team();
                team.setName(rs.getString("team"));
                player.setTeam(team);

                players.add(player);
            }
        }

        return players;
    }

}

