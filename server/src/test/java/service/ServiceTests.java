package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import passoff.model.*;

import java.util.*;

public class ServiceTests {
    private static MemoryUserDao userDao;
    private static MemoryGameDao gameDao;
    private static MemoryAuthDao authDao;

    private static UserService userService;
    private static ClearService clearService;
    private static GameService gameService;

    @BeforeEach
    public void init() {
        userDao = new MemoryUserDao();
        gameDao = new MemoryGameDao();
        authDao = new MemoryAuthDao();

        userService = new UserService(userDao, authDao);
        clearService = new ClearService(userDao, gameDao, authDao);
        gameService = new GameService(userDao, gameDao, authDao);
    }

    @Test
    @Order(1)
    @DisplayName("Registration Positive")
    public void registerSuccess() {
        UserService.RegisterResult res = userService.register(new UserService.RegisterRequest("username", "password", "email"));
        Assertions.assertEquals("username", userDao.getdatabase().get("username").username());
        Assertions.assertEquals("username", res.username());
        Assertions.assertNull(res.message());
    }

    @Test
    @Order(2)
    @DisplayName("Registration Negative")
    public void registerFailure() {
        userService.register(new UserService.RegisterRequest("username", "password", "email"));
        UserService.RegisterResult res = userService.register(new UserService.RegisterRequest("username", "password", "email"));
        Assertions.assertNull(res.username());
        Assertions.assertEquals("Error: already taken", res.message());
    }

    @Test
    @Order(3)
    @DisplayName("Login Positive")
    public void loginSuccess() {
        userService.register(new UserService.RegisterRequest("username", "password", "email"));
        UserService.LoginResult res = userService.login(new UserService.LoginRequest("username", "password"));
        Assertions.assertEquals("username", userDao.getdatabase().get("username").username());
        Assertions.assertEquals("username", res.username());
        Assertions.assertNotNull(res.authToken());
        Assertions.assertNull(res.message());
    }

    @Test
    @Order(4)
    @DisplayName("Login Negative")
    public void loginFailure() {
        UserService.LoginResult res = userService.login(new UserService.LoginRequest("username", "password"));
        Assertions.assertNotNull(res.message());
        Assertions.assertNull(res.authToken());

        userService.register(new UserService.RegisterRequest("username", "password", "email"));
        res = userService.login(new UserService.LoginRequest("username", "password1"));
        Assertions.assertNull(res.authToken());
        Assertions.assertNotNull(res.message());
    }

    @Test
    @Order(5)
    @DisplayName("Logout Positive")
    public void logoutSuccess() {
        userService.register(new UserService.RegisterRequest("username", "password", "email"));
        UserService.LoginResult log = userService.login(new UserService.LoginRequest("username", "password"));
        UserService.LogoutResult res = userService.logout(new UserService.LogoutRequest(log.authToken()));
        Assertions.assertNull(res.message());
        Assertions.assertNull(authDao.getdatabase().get(log.authToken()));
    }

    @Test
    @Order(6)
    @DisplayName("Logout Negative")
    public void logoutFailure() {
        userService.register(new UserService.RegisterRequest("username", "password", "email"));

        UserService.LogoutResult res = userService.logout(new UserService.LogoutRequest("string"));
        Assertions.assertNotNull(res.message());


        UserService.LoginResult log = userService.login(new UserService.LoginRequest("username", "password"));
        res = userService.logout(new UserService.LogoutRequest(log.authToken() + "1"));
        Assertions.assertNotNull(res.message());
        Assertions.assertNotNull(authDao.getdatabase().get(log.authToken()));
    }

    @Test
    @Order(7)
    @DisplayName("Create Positive")
    public void createSuccess() {
        userService.register(new UserService.RegisterRequest("username", "password", "email"));
        UserService.LoginResult log = userService.login(new UserService.LoginRequest("username", "password"));

        GameService.CreateResult res = gameService.createGame(new GameService.CreateRequest(log.authToken(), "game"));
        Assertions.assertNotNull(res.gameID());
        Assertions.assertNull(res.message());

        GameData game = gameDao.getdatabase().get(res.gameID());
        Assertions.assertEquals("game", game.gameName());
        Assertions.assertNull(game.blackUsername());
        Assertions.assertNull(game.whiteUsername());
    }

    @Test
    @Order(8)
    @DisplayName("Create Negative")
    public void createFailure() {
        userService.register(new UserService.RegisterRequest("username", "password", "email"));
        userService.login(new UserService.LoginRequest("username", "password"));

        GameService.CreateResult res = gameService.createGame(new GameService.CreateRequest("name", "game"));
        Assertions.assertNull(res.gameID());
        Assertions.assertNotNull(res.message());

        GameData game = gameDao.getdatabase().get(1234);
        Assertions.assertNull(game);

    }


    @Test
    @Order(9)
    @DisplayName("List Positive")
    public void listSuccess() {
        userService.register(new UserService.RegisterRequest("username", "password", "email"));
        UserService.LoginResult log = userService.login(new UserService.LoginRequest("username", "password"));

        GameService.CreateResult g1 = gameService.createGame(new GameService.CreateRequest(log.authToken(), "game"));
        GameService.CreateResult g2 = gameService.createGame(new GameService.CreateRequest(log.authToken(), "game2"));

        GameService.ListResult res = gameService.listGames(new GameService.ListRequest(log.authToken()));
        Assertions.assertNull(res.message());

        GameData game1 = gameDao.getdatabase().get(g1.gameID());
        GameData game2 = gameDao.getdatabase().get(g2.gameID());

        ArrayList<GameData> games = new ArrayList<>();
        games.add(game1);
        games.add(game2);
        Assertions.assertEquals(res.games(), games);
    }
    @Test
    @Order(10)
    @DisplayName("List Negative")
    public void listFailure() {
        userService.register(new UserService.RegisterRequest("username", "password", "email"));
        UserService.LoginResult log = userService.login(new UserService.LoginRequest("username", "password"));

        GameService.CreateResult g1 = gameService.createGame(new GameService.CreateRequest(log.authToken(), "game"));
        GameService.CreateResult g2 = gameService.createGame(new GameService.CreateRequest(log.authToken(), "game2"));

        GameService.ListResult res = gameService.listGames(new GameService.ListRequest("1"));
        Assertions.assertNotNull(res.message());

        GameData game1 = gameDao.getdatabase().get(g1.gameID());
        GameData game2 = gameDao.getdatabase().get(g2.gameID());

        ArrayList<GameData> games = new ArrayList<>();
        games.add(game2);
        games.add(game1);
        Assertions.assertNotEquals(res.games(), games);
    }

    @Test
    @Order(11)
    @DisplayName("Join Positive")
    public void joinSuccess() {
        userService.register(new UserService.RegisterRequest("username", "password", "email"));
        UserService.LoginResult log = userService.login(new UserService.LoginRequest("username", "password"));

        GameService.CreateResult g1 = gameService.createGame(new GameService.CreateRequest(log.authToken(), "game"));

        GameService.JoinResult res = gameService.joinGame(new GameService.JoinRequest(log.authToken(), "WHITE", g1.gameID() ));
        Assertions.assertNull(res.message());

        GameData game = gameDao.getdatabase().get(g1.gameID());
        Assertions.assertEquals("username", game.whiteUsername());
    }

    @Test
    @Order(12)
    @DisplayName("Join Negative")
    public void joinNegative() {
        userService.register(new UserService.RegisterRequest("username", "password", "email"));
        UserService.LoginResult log = userService.login(new UserService.LoginRequest("username", "password"));

        GameService.CreateResult g1 = gameService.createGame(new GameService.CreateRequest(log.authToken(), "game"));

        GameService.JoinResult res = gameService.joinGame(new GameService.JoinRequest("F", "WHITE", g1.gameID() ));
        Assertions.assertNotNull(res.message());

        //Note that checking if the playerColor is valid occurs in the handler

        res = gameService.joinGame(new GameService.JoinRequest(log.authToken(), "WHITE", 123 ));
        Assertions.assertNotNull(res.message());

        gameService.joinGame(new GameService.JoinRequest(log.authToken(), "WHITE", g1.gameID()));

        userService.register(new UserService.RegisterRequest("username2", "password2", "email2"));
        UserService.LoginResult log2 = userService.login(new UserService.LoginRequest("username2", "password2"));
        res = gameService.joinGame(new GameService.JoinRequest(log2.authToken(), "WHITE", g1.gameID() ));
        Assertions.assertNotNull(res.message());
    }

    @Test
    @Order(13)
    @DisplayName("Clear Positive")
    public void clearSuccess() {
        userService.register(new UserService.RegisterRequest("username", "password", "email"));
        UserService.LoginResult log = userService.login(new UserService.LoginRequest("username", "password"));
        gameService.createGame(new GameService.CreateRequest(log.authToken(), "game"));
        clearService.clear();

        HashMap<String, UserData> users = userDao.getdatabase();
        HashMap<String, AuthData> auths = authDao.getdatabase();
        HashMap<Integer, GameData> games = gameDao.getdatabase();
        Assertions.assertTrue(users.isEmpty());
        Assertions.assertTrue(auths.isEmpty());
        Assertions.assertTrue(games.isEmpty());

    }
}
