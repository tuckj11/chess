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
    public void createUser(UserData userData) {

    }

    @Override
    public void clear() {

    }
}
