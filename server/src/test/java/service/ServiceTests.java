package service;

import chess.ChessGame;
import dataaccess.*;
import service.UserService;
import org.junit.jupiter.api.*;
import passoff.model.*;
import passoff.server.TestServerFacade;
import server.Server;

import java.net.HttpURLConnection;
import java.util.*;

public class ServiceTests {
    private static MemoryUserDao userDao;
    private static MemoryGameDao gameDao;
    private static MemoryAuthDao authDao;

    private static UserService userService;
    private static ClearService clearService;
    private static GameService gameService;

    @BeforeAll
    public static void init() {
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
    }
}
