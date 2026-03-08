package dataaccess;

import model.UserData;

import java.sql.SQLException;

public class SQLUserDao implements UserDao{
    @Override
    public UserData getUser(String username) throws DataAccessException {
        String sql = "SELECT username, password, email FROM users WHERE username=?";
        try (var conn = DatabaseManager.getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            var rs = ps.executeQuery();
            if(rs.next()) {
                return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
            }
            else{
                return null;
            }
        }
        catch (SQLException e) {
            throw new DataAccessException("failed to get user");
        }
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException{
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (var conn = DatabaseManager.getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setString(1, userData.username());
            ps.setString(2, userData.password());
            ps.setString(3, userData.email());
            ps.executeQuery();
        }
        catch (SQLException e) {
            throw new DataAccessException("failed to register user");

        }
    }

    @Override
    public void clear() throws DataAccessException{
        String sql = "TRUNCATE TABLE users";
        try (var conn = DatabaseManager.getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.executeQuery();
        } catch (SQLException e) {
            throw new DataAccessException("failed to clear users");
        }
    }
}
