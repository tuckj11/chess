package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class SQLGameDao implements GameDao{
    private static final Gson GSON = new Gson();

    @Override
    public ArrayList<GameData> getGames() throws DataAccessException {
        String sql = "SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM games";
        ArrayList<GameData> results = new ArrayList<>();

        try (var conn = DatabaseManager.getConnection()) {
             var ps = conn.prepareStatement(sql);
             var rs = ps.executeQuery();

             while(rs.next()) {
                 int id = rs.getInt("gameID");
                 String whiteUsername = rs.getString("whiteUsername");
                 String blackUsername = rs.getString("blackUsername");
                 String gameName = rs.getString("gameName");
                 String json = rs.getString("chessGame");
                 ChessGame game = GSON.fromJson(json, ChessGame.class);
                 results.add(new GameData(id, whiteUsername, blackUsername, gameName, game));
             }
             return results;

        } catch (SQLException e) {
            throw new DataAccessException("failed to get games");
        }
    }

    @Override
    public Integer makeGame(String gameName) throws DataAccessException{
        String sql = "INSERT INTO games (gameName, chessGame) VALUES (?, ?)";
        try (var conn = DatabaseManager.getConnection(); var ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, gameName);
            String game = GSON.toJson(new ChessGame());
            ps.setString(2, game);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            else {
                return null;
            }
        }
        catch (SQLException e) {
            throw new DataAccessException("failed to make game");

        }
    }

    @Override
    public int connectToGame(String username, Integer gameID, String playerColor) throws DataAccessException {
        String selectSql = "SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM games WHERE gameID=?";
        String updateSql = "UPDATE games SET whiteUsername = ?, blackUsername = ? WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection()) {
            var selectPs = conn.prepareStatement(selectSql);
            var updatePs = conn.prepareStatement(updateSql);
            selectPs.setInt(1, gameID);
            var rs = selectPs.executeQuery();
            if (rs.next()) {
                if (playerColor.equals("WHITE")) {
                    if (rs.getString("whiteUsername") != null) {
                        return 2;
                    } else {
                        updatePs.setString(1, username);
                        updatePs.setString(2, rs.getString("blackUsername"));
                    }
                } else {
                    if (rs.getString("blackUsername") != null) {
                        return 2;
                    } else {
                        updatePs.setString(1, rs.getString("whiteUsername"));
                        updatePs.setString(2, username);
                    }
                }
            } else {
                return 1;
            }
            updatePs.setInt(3, gameID);
            updatePs.executeUpdate();
            return 0;
        } catch (SQLException e) {
            throw new DataAccessException("failed to update game");
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "TRUNCATE TABLE games";
        try (var conn = DatabaseManager.getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.execute();
        } catch (SQLException e) {
            throw new DataAccessException("failed to clear games");
        }
    }
}
