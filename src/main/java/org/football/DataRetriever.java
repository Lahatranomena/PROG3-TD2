package org.football;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    DBConnection connection = new DBConnection();

    Team findTeamById(Integer id) {
        List<Player> players = new ArrayList<Player>();
        String sql = """
                SELECT
                    team.id AS team_id, team.name AS team_name, team.continent, player.id AS player_id,
                    player.name AS player_name, player.age AS age, player.position AS positions FROM team 
                    LEFT JOIN player ON team.id = player.id_team
                WHERE team.id = ?;
                
                """;

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

    List<Player> createPlayers(List<Player> newPlayers)  throws SQLException {
        throw new RuntimeException();
    }

    Team saveTeam(Team teamToSave) throws SQLException {
        throw new RuntimeException();
    }

    List<Team> findTeamsByPlayerName(String playerName)  throws SQLException {
        throw new RuntimeException();
    }

    List<Player> findPlayersByCriteria(String playerName,
                                       PlayerPositionEnum position, String teamName, ContinentEnum
                                               continent, int page, int size)   throws SQLException {
        throw new RuntimeException();
    }
}
