package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.ArrayList;

public class DataAccessTests {
    private static SQLUserDao userDao;
    private static SQLGameDao gameDao;
    private static SQLAuthDao authDao;

    @BeforeEach
    public void init() throws DataAccessException {
        DatabaseManager.createDatabase();

        userDao = new SQLUserDao();
        gameDao = new SQLGameDao();
        authDao = new SQLAuthDao();

        authDao.clear();
        userDao.clear();
        gameDao.clear();
    }

    @Test
    @Order(1)
    @DisplayName("Create User Positive")
    public void createUserSuccess() throws DataAccessException {
        userDao.createUser(new UserData("username", "password", "email"));
        String sql = "SELECT username, password, email FROM users WHERE username=?";
        try (var conn = DatabaseManager.getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setString(1, "username");
            var rs = ps.executeQuery();
            rs.next();
            Assertions.assertEquals("username", rs.getString("username"));
            Assertions.assertEquals("password", rs.getString("password"));
            Assertions.assertEquals("email", rs.getString("email"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(2)
    @DisplayName("Create User Negative")
    public void createUserFailure() throws DataAccessException {
        userDao.createUser(new UserData("username", "password", "email"));
        Exception e = Assertions.assertThrows(Exception.class, () -> userDao.createUser(new UserData("username", "password", "email")));
        Assertions.assertTrue(e.getMessage().contains("failed to register user"));
    }

    @Test
    @Order(3)
    @DisplayName("Get User Positive")
    public void getUserPositive() throws DataAccessException {
        userDao.createUser(new UserData("username", "password", "email"));
        UserData user = userDao.getUser("username");
        Assertions.assertEquals("username", user.username());
        Assertions.assertEquals("password", user.password());
        Assertions.assertEquals("email", user.email());

    }

    @Test
    @Order(4)
    @DisplayName("Get User Negative")
    public void getUserFailure() throws DataAccessException {
        UserData user = userDao.getUser("username");
        Assertions.assertNull(user);
    }

    @Test
    @Order(5)
    @DisplayName("User Clear Positive")
    public void userClearSuccess() throws DataAccessException {
        userDao.createUser(new UserData("username", "password", "email"));
        userDao.clear();
        UserData user = userDao.getUser("username");
        Assertions.assertNull(user);
    }

    @Test
    @Order(6)
    @DisplayName("Create Auth Positive")
    public void createAuthSuccess() throws DataAccessException {
        AuthData data = authDao.createAuth("username");
        String sql = "SELECT authToken, username FROM auths WHERE authToken=?";
        try (var conn = DatabaseManager.getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setString(1, data.authToken());
            var rs = ps.executeQuery();
            rs.next();
            Assertions.assertEquals("username", rs.getString("username"));
            Assertions.assertEquals(data.authToken(), rs.getString("authToken"));

        } catch (SQLException e) {
            throw new DataAccessException("failed to verify auth");
        }
    }

    @Test
    @Order(7)
    @DisplayName("Create Auth Negative")
    public void createAuthFailure() throws DataAccessException {
        authDao.createAuth("username");
        String sql = "SELECT authToken, username FROM auths WHERE authToken=?";
        try (var conn = DatabaseManager.getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setString(1, "username");
            var rs = ps.executeQuery();
            Assertions.assertFalse(rs.next());

        } catch (SQLException e) {
            throw new DataAccessException("failed to verify auth");
        }
    }

    @Test
    @Order(8)
    @DisplayName("Verify Auth Positive")
    public void verifyAuthSuccess() throws DataAccessException {
        AuthData data = authDao.createAuth("username");
        AuthData check = authDao.verifyAuth(data.authToken());
        Assertions.assertEquals(data.authToken(), check.authToken());
        Assertions.assertEquals(data.username(), check.username());
    }


    @Test
    @Order(9)
    @DisplayName("Verify Auth Negative")
    public void verifyAuthFailure() throws DataAccessException {
        AuthData data = authDao.verifyAuth("username");
        Assertions.assertNull(data);
    }

    @Test
    @Order(10)
    @DisplayName("Delete Auth Positive")
    public void deleteAuthSuccess() throws DataAccessException {
        AuthData data = authDao.createAuth("username");
        authDao.deleteAuth(data);
        String sql = "SELECT authToken, username FROM auths WHERE authToken=?";
        try (var conn = DatabaseManager.getConnection(); var ps = conn.prepareStatement(sql)) {
            ps.setString(1, data.authToken());
            var rs = ps.executeQuery();
            Assertions.assertFalse(rs.next());
        } catch (SQLException e) {
            throw new DataAccessException("failed to verify auth");
        }
    }

    @Test
    @Order(11)
    @DisplayName("Delete Auth Negative")
    public void deleteAuthFailure() {
        Exception e = Assertions.assertThrows(Exception.class, () -> authDao.deleteAuth(new AuthData("token", "username")));
        Assertions.assertTrue(e.getMessage().contains("failed to delete auth"));
    }

    @Test
    @Order(12)
    @DisplayName("Auth Clear Positive")
    public void authClearSuccess() throws DataAccessException {
        AuthData data = authDao.createAuth("username");
        authDao.clear();
        AuthData check = authDao.verifyAuth(data.authToken());
        Assertions.assertNull(check);
    }

    @Test
    @Order(13)
    @DisplayName("Make Game Positive")
    public void makeGameSuccess() throws DataAccessException {
        Integer id = gameDao.makeGame("game");
        String selectSql = "SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM games WHERE gameID=?";
        try (var conn = DatabaseManager.getConnection()) {
            var selectPs = conn.prepareStatement(selectSql);
            selectPs.setInt(1, id);
            var rs = selectPs.executeQuery();
            Assertions.assertTrue(rs.next());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(14)
    @DisplayName("Make Game Negative")
    public void makeGameFailure() throws DataAccessException {
        gameDao.makeGame("game");
        String selectSql = "SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM games WHERE gameID=?";
        try (var conn = DatabaseManager.getConnection()) {
            var selectPs = conn.prepareStatement(selectSql);
            selectPs.setInt(1, 10);
            var rs = selectPs.executeQuery();
            Assertions.assertFalse(rs.next());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(15)
    @DisplayName("Get Games Positive")
    public void getGamesSuccess() throws DataAccessException {
        gameDao.makeGame("game");
        ArrayList<GameData> games = gameDao.getGames();
        Assertions.assertEquals("game", games.getFirst().gameName());
    }

    @Test
    @Order(16)
    @DisplayName("Get Games Negative")
    public void getGamesFailure() throws DataAccessException {
        ArrayList<GameData> games = gameDao.getGames();
        Assertions.assertThrows(Exception.class, games::getFirst);
    }

    @Test
    @Order(17)
    @DisplayName("Connect To Game Positive")
    public void connectToGameSuccess() throws DataAccessException {
        Integer id = gameDao.makeGame("game");
        Integer result = gameDao.connectToGame("username", id, "WHITE");
        Assertions.assertEquals(0, result);
    }

    @Test
    @Order(17)
    @DisplayName("Connect To Game Negative")
    public void connectToGameFailure() throws DataAccessException {
        Integer id = gameDao.makeGame("game");
        gameDao.connectToGame("username", id, "WHITE");
        Integer result = gameDao.connectToGame("username", id, "WHITE");
        Assertions.assertEquals(2, result);

        Integer result2 = gameDao.connectToGame("username", 4, "WHITE");
        Assertions.assertEquals(1, result2);
    }

    @Test
    @Order(18)
    @DisplayName("Game Clear Positive")
    public void gameClearSuccess() throws DataAccessException {
        gameDao.makeGame("game");
        gameDao.clear();
        ArrayList<GameData> games = gameDao.getGames();
        Assertions.assertEquals(0, games.size());
    }
}
