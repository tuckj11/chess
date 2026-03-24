
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
import service.ClearService;
import service.GameService;
import service.UserService;

public class ServerFacadeTests {

    private static Server server;
    private ServerFacade serverFacade;

    @BeforeEach
    public void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port);
        serverFacade.clear();
    }

    @AfterEach
    void stopServer() {
        server.stop();
    }


    @Test
    @Order(1)
    @DisplayName("Registration Positive")
    public void registrationSuccess() {
        UserService.RegisterResult res = serverFacade.register(new UserService.RegisterRequest("username", "password", "email"));
        Assertions.assertEquals("username", res.username());
    }

    @Test
    @Order(2)
    @DisplayName("Registration Negative")
    public void registerFailure() {
        serverFacade.register(new UserService.RegisterRequest("username", "password", "email"));
        Exception e = Assertions.assertThrows(Exception.class, () -> serverFacade.register(new UserService.RegisterRequest("username", "password", "email")));
        Assertions.assertTrue(e.getMessage().contains("403"));
    }


    @Test
    @Order(3)
    @DisplayName("Login Positive")
    public void loginSuccess() {
        serverFacade.register(new UserService.RegisterRequest("username", "password", "email"));
        UserService.LoginResult res = serverFacade.login(new UserService.LoginRequest("username", "password"));
        Assertions.assertEquals("username", res.username());
        Assertions.assertNotNull(res.authToken());
        Assertions.assertNull(res.message());
    }

    @Test
    @Order(4)
    @DisplayName("Login Negative")
    public void loginFailure() {
        Exception e = Assertions.assertThrows(Exception.class, () -> serverFacade.login(new UserService.LoginRequest("username", "password")));
        Assertions.assertTrue(e.getMessage().contains("401"));

        serverFacade.register(new UserService.RegisterRequest("username", "password", "email"));
        Exception e2 = Assertions.assertThrows(Exception.class, () -> serverFacade.login(new UserService.LoginRequest("username", "password1")));
        Assertions.assertTrue(e2.getMessage().contains("401"));
    }

    @Test
    @Order(5)
    @DisplayName("Logout Positive")
    public void logoutSuccess() {
        serverFacade.register(new UserService.RegisterRequest("username", "password", "email"));
        UserService.LoginResult log = serverFacade.login(new UserService.LoginRequest("username", "password"));
        UserService.LogoutResult res = serverFacade.logout(new UserService.LogoutRequest(log.authToken()));
        Assertions.assertNull(res.message());
    }

    @Test
    @Order(6)
    @DisplayName("Logout Negative")
    public void logoutFailure() {
        serverFacade.register(new UserService.RegisterRequest("username", "password", "email"));

        Exception e = Assertions.assertThrows(Exception.class, () -> serverFacade.logout(new UserService.LogoutRequest("string")));
        Assertions.assertTrue(e.getMessage().contains("401"));

        UserService.LoginResult log = serverFacade.login(new UserService.LoginRequest("username", "password"));

        Exception ex = Assertions.assertThrows(Exception.class, () -> serverFacade.logout(new UserService.LogoutRequest(log.authToken() + "1")));
        Assertions.assertTrue(ex.getMessage().contains("401"));
    }


    @Test
    @Order(7)
    @DisplayName("Create Positive")
    public void createSuccess() {
        serverFacade.register(new UserService.RegisterRequest("username", "password", "email"));
        UserService.LoginResult log = serverFacade.login(new UserService.LoginRequest("username", "password"));

        GameService.CreateResult res = serverFacade.createGame(new GameService.CreateRequest(log.authToken(), "game"));
        Assertions.assertNotNull(res.gameID());
        Assertions.assertNull(res.message());
    }

    @Test
    @Order(8)
    @DisplayName("Create Negative")
    public void createFailure() {
        serverFacade.register(new UserService.RegisterRequest("username", "password", "email"));
        serverFacade.login(new UserService.LoginRequest("username", "password"));

        Exception e = Assertions.assertThrows(Exception.class, () -> serverFacade.createGame(new GameService.CreateRequest("name", "game")));
        Assertions.assertTrue(e.getMessage().contains("401"));

    }


    @Test
    @Order(9)
    @DisplayName("List Positive")
    public void listSuccess() {
        serverFacade.register(new UserService.RegisterRequest("username", "password", "email"));
        UserService.LoginResult log = serverFacade.login(new UserService.LoginRequest("username", "password"));

        serverFacade.createGame(new GameService.CreateRequest(log.authToken(), "game"));
        serverFacade.createGame(new GameService.CreateRequest(log.authToken(), "game2"));

        GameService.ListResult res = serverFacade.listGames(new GameService.ListRequest(log.authToken()));
        Assertions.assertNull(res.message());

    }

    @Test
    @Order(10)
    @DisplayName("List Negative")
    public void listFailure() {
        serverFacade.register(new UserService.RegisterRequest("username", "password", "email"));
        UserService.LoginResult log = serverFacade.login(new UserService.LoginRequest("username", "password"));

        serverFacade.createGame(new GameService.CreateRequest(log.authToken(), "game"));
        serverFacade.createGame(new GameService.CreateRequest(log.authToken(), "game2"));

        Exception e = Assertions.assertThrows(Exception.class, () -> serverFacade.listGames(new GameService.ListRequest("1")));
        Assertions.assertTrue(e.getMessage().contains("401"));

    }

    @Test
    @Order(11)
    @DisplayName("Join Positive")
    public void joinSuccess() {
        serverFacade.register(new UserService.RegisterRequest("username", "password", "email"));
        UserService.LoginResult log = serverFacade.login(new UserService.LoginRequest("username", "password"));

        GameService.CreateResult g1 = serverFacade.createGame(new GameService.CreateRequest(log.authToken(), "game"));

        GameService.JoinResult res = serverFacade.joinGame(new GameService.JoinRequest(log.authToken(), "WHITE", g1.gameID() ));
        Assertions.assertNull(res.message());
    }

    @Test
    @Order(12)
    @DisplayName("Join Negative")
    public void joinNegative() {
        serverFacade.register(new UserService.RegisterRequest("username", "password", "email"));
        UserService.LoginResult log = serverFacade.login(new UserService.LoginRequest("username", "password"));

        GameService.CreateResult g1 = serverFacade.createGame(new GameService.CreateRequest(log.authToken(), "game"));

        Exception e = Assertions.assertThrows(Exception.class, () -> serverFacade.joinGame(new GameService.JoinRequest("F", "WHITE", g1.gameID())));
        Assertions.assertTrue(e.getMessage().contains("401"));

        //Note that checking if the playerColor is valid occurs in the handler

        Exception e2 = Assertions.assertThrows(Exception.class, () -> serverFacade.joinGame(new GameService.JoinRequest(log.authToken(), "WHITE", 123)));
        Assertions.assertTrue(e2.getMessage().contains("400"));

        serverFacade.joinGame(new GameService.JoinRequest(log.authToken(), "WHITE", g1.gameID()));

        serverFacade.register(new UserService.RegisterRequest("username2", "password2", "email2"));
        UserService.LoginResult log2 = serverFacade.login(new UserService.LoginRequest("username2", "password2"));

        Exception e3 = Assertions.assertThrows(Exception.class, () -> serverFacade.joinGame(new GameService.JoinRequest(log2.authToken(), "WHITE", g1.gameID())));
        Assertions.assertTrue(e3.getMessage().contains("403"));
    }

    @Test
    @Order(13)
    @DisplayName("Clear Positive")
    public void clearSuccess() {
        serverFacade.register(new UserService.RegisterRequest("username", "password", "email"));
        UserService.LoginResult log = serverFacade.login(new UserService.LoginRequest("username", "password"));
        serverFacade.createGame(new GameService.CreateRequest(log.authToken(), "game"));
        ClearService.ClearResult res = serverFacade.clear();
        Assertions.assertNull(res.message());
    }

}
