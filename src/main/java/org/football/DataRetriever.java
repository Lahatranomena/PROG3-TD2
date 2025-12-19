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
                    player.name AS player_name, player.age AS age, player.position AS positions FROM team\s
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
                        player.setPosition(
                                PlayerPositionEnum.valueOf(resultSet.getString("positions"))
                        );

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
                	age, player.position AS positions, player.id_team AS team FROM
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
                   INSERT INTO player player.id AS player_id, player.name AS player_name,\s
                   player.age AS age, player.position AS position VALUES (?, ?, ?, ?)
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
                    statement.setString(4, player.getPosition().name());
                    statement.executeUpdate();
                }
            }
            conn.commit();
            System.out.println("Success");
            return newPlayers;
        } catch (Exception e) {
            throw new RuntimeException("Transaction cancel : ", e);
        }
    }

    Team saveTeam(Team teamToSave) throws SQLException {

        String sql = """
                SELECT team.id, team.name FROM team where id = ?;
                """;

        try (PreparedStatement statement = connection.getDBConnection().prepareStatement(sql)) {
            statement.setInt(1, teamToSave.getId());
        }
        throw new RuntimeException();
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


    List<Player> findPlayersByCriteria(String playerName, PlayerPositionEnum position, String teamName,
            ContinentEnum continent, int page, int size
    ) throws SQLException {
        Team team = new Team();

        List<Player> players = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
                SELECT player.id AS player_id, player.name AS player_name, player.age AS age, player.position AS position,\s
                team.name AS team FROM  player LEFT JOIN team ON player.id_team = team.id
                where 1 = 1
               \s""");

        List<Object> parameters = new ArrayList<>();

        if (playerName != null) {
            sql.append("and player.name like ? ");
            parameters.add("%" + playerName + "%");
        }
        if (position != null) {
            sql.append("and player.position = ? ");
            parameters.add(position);
        }
        if (teamName != null) {
            sql.append("and team.name = ? ");
            parameters.add(teamName);
        }
        if (continent != null) {
            sql.append("and player.position = ? ");
            parameters.add(continent);
        }
        sql.append("limit ? offset ?");

        int offset = (page - 1) * size;
        parameters.add(size);
        parameters.add(offset);

        try (Connection conn = connection.getDBConnection();
             PreparedStatement statement = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                if (rs.getInt("player_id") != 0) {
                    Player player = new Player();
                    player.setId(rs.getInt("player_id"));
                    player.setName(rs.getString("player_name"));
                    player.setAge(rs.getInt("age"));
                    player.setPosition(PlayerPositionEnum.valueOf(rs.getString("position")));
                    team.setName(rs.getString("team"));
                    player.setTeam(team);
                    players.add(player);
                }
            }
        }
        return players;
    }
}

