package dataaccess;

import model.AuthData;
import model.UserData;

import java.sql.SQLException;
import java.util.UUID;


public class SQLAuthDao implements AuthDao{
    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        String sql = "INSERT INTO auths (authToken, username) VALUES (?, ?)";
        try (var conn = DatabaseManager.getConnection(); var ps = conn.prepareStatement(sql)) {
            String token = UUID.randomUUID().toString();
            ps.setString(1, token);
            ps.setString(2, username);
            ps.executeUpdate();
            return new AuthData(token, username);
        }
        catch (SQLException e) {
            throw new DataAccessException("Unable to add authToken");
        }
    }

    @Override
    public AuthData verifyAuth(String authToken) throws DataAccessException{
        String sql = "SELECT authToken, username FROM auths WHERE authToken=?";
        try (var conn = DatabaseManager.getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setString(1, authToken);
            var rs = ps.executeQuery();
            if(rs.next()) {
                return new AuthData(rs.getString("authToken"), rs.getString("username"));
            }
            else{
                return null;
            }
        }
        catch (SQLException e) {
            throw new DataAccessException("failed to verify auth");
        }
    }

    @Override
    public void deleteAuth(AuthData authData) throws DataAccessException{
        String sql = "DELETE FROM auths WHERE authToken=?";
        try (var conn = DatabaseManager.getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setString(1, authData.authToken());
            var rs = ps.executeQuery();
        }
        catch (SQLException e) {
            throw new DataAccessException("failed to delete auth");
        }
    }

    @Override
    public void clear() throws DataAccessException{
        String sql = "TRUNCATE TABLE auths";
        try (var conn = DatabaseManager.getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.execute();
        } catch (SQLException e) {
            throw new DataAccessException("failed to clear auths");
        }
    }
}
