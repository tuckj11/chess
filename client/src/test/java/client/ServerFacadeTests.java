
import org.junit.jupiter.api.*;
import requests.Requests;
import results.Results;
import server.Server;
import client.ServerFacade;

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
        Results.RegisterResult res = serverFacade.register(new Requests.RegisterRequest("username", "password", "email"));
        Assertions.assertEquals("username", res.username());
    }

    @Test
    @Order(2)
    @DisplayName("Registration Negative")
    public void registerFailure() {
        serverFacade.register(new Requests.RegisterRequest("username", "password", "email"));
        Exception e = Assertions.assertThrows(Exception.class, () ->
                serverFacade.register(new Requests.RegisterRequest("username", "password", "email")));
        Assertions.assertTrue(e.getMessage().contains("403"));
    }


    @Test
    @Order(3)
    @DisplayName("Login Positive")
    public void loginSuccess() {
        serverFacade.register(new Requests.RegisterRequest("username", "password", "email"));
        Results.LoginResult res = serverFacade.login(new Requests.LoginRequest("username", "password"));
        Assertions.assertEquals("username", res.username());
        Assertions.assertNotNull(res.authToken());
        Assertions.assertNull(res.message());
    }

    @Test
    @Order(4)
    @DisplayName("Login Negative")
    public void loginFailure() {
        Exception e = Assertions.assertThrows(Exception.class, () -> serverFacade.login(new Requests.LoginRequest("username", "password")));
        Assertions.assertTrue(e.getMessage().contains("401"));

        serverFacade.register(new Requests.RegisterRequest("username", "password", "email"));
        Exception e2 = Assertions.assertThrows(Exception.class, () -> serverFacade.login(new Requests.LoginRequest("username", "password1")));
        Assertions.assertTrue(e2.getMessage().contains("401"));
    }

    @Test
    @Order(5)
    @DisplayName("Logout Positive")
    public void logoutSuccess() {
        serverFacade.register(new Requests.RegisterRequest("username", "password", "email"));
        Results.LoginResult log = serverFacade.login(new Requests.LoginRequest("username", "password"));
        Results.LogoutResult res = serverFacade.logout(new Requests.LogoutRequest(log.authToken()));
        Assertions.assertNull(res.message());
    }

    @Test
    @Order(6)
    @DisplayName("Logout Negative")
    public void logoutFailure() {
        serverFacade.register(new Requests.RegisterRequest("username", "password", "email"));

        Exception e = Assertions.assertThrows(Exception.class, () -> serverFacade.logout(new Requests.LogoutRequest("string")));
        Assertions.assertTrue(e.getMessage().contains("401"));

        Results.LoginResult log = serverFacade.login(new Requests.LoginRequest("username", "password"));

        Exception ex = Assertions.assertThrows(Exception.class, () -> serverFacade.logout(new Requests.LogoutRequest(log.authToken() + "1")));
        Assertions.assertTrue(ex.getMessage().contains("401"));
    }


    @Test
    @Order(7)
    @DisplayName("Create Positive")
    public void createSuccess() {
        serverFacade.register(new Requests.RegisterRequest("username", "password", "email"));
        Results.LoginResult log = serverFacade.login(new Requests.LoginRequest("username", "password"));

        Results.CreateResult res = serverFacade.createGame(new Requests.CreateRequest(log.authToken(), "game"));
        Assertions.assertNotNull(res.gameID());
        Assertions.assertNull(res.message());
    }

    @Test
    @Order(8)
    @DisplayName("Create Negative")
    public void createFailure() {
        serverFacade.register(new Requests.RegisterRequest("username", "password", "email"));
        serverFacade.login(new Requests.LoginRequest("username", "password"));

        Exception e = Assertions.assertThrows(Exception.class, () -> serverFacade.createGame(new Requests.CreateRequest("name", "game")));
        Assertions.assertTrue(e.getMessage().contains("401"));

    }


    @Test
    @Order(9)
    @DisplayName("List Positive")
    public void listSuccess() {
        serverFacade.register(new Requests.RegisterRequest("username", "password", "email"));
        Results.LoginResult log = serverFacade.login(new Requests.LoginRequest("username", "password"));

        serverFacade.createGame(new Requests.CreateRequest(log.authToken(), "game"));
        serverFacade.createGame(new Requests.CreateRequest(log.authToken(), "game2"));

        Results.ListResult res = serverFacade.listGames(new Requests.ListRequest(log.authToken()));
        Assertions.assertNull(res.message());

    }

    @Test
    @Order(10)
    @DisplayName("List Negative")
    public void listFailure() {
        serverFacade.register(new Requests.RegisterRequest("username", "password", "email"));
        Results.LoginResult log = serverFacade.login(new Requests.LoginRequest("username", "password"));

        serverFacade.createGame(new Requests.CreateRequest(log.authToken(), "game"));
        serverFacade.createGame(new Requests.CreateRequest(log.authToken(), "game2"));

        Exception e = Assertions.assertThrows(Exception.class, () -> serverFacade.listGames(new Requests.ListRequest("1")));
        Assertions.assertTrue(e.getMessage().contains("401"));

    }

    @Test
    @Order(11)
    @DisplayName("Join Positive")
    public void joinSuccess() {
        serverFacade.register(new Requests.RegisterRequest("username", "password", "email"));
        Results.LoginResult log = serverFacade.login(new Requests.LoginRequest("username", "password"));

        Results.CreateResult g1 = serverFacade.createGame(new Requests.CreateRequest(log.authToken(), "game"));

        Results.JoinResult res = serverFacade.joinGame(new Requests.JoinRequest(log.authToken(), "WHITE", g1.gameID() ));
        Assertions.assertNull(res.message());
    }

    @Test
    @Order(12)
    @DisplayName("Join Negative")
    public void joinNegative() {
        serverFacade.register(new Requests.RegisterRequest("username", "password", "email"));
        Results.LoginResult log = serverFacade.login(new Requests.LoginRequest("username", "password"));

        Results.CreateResult g1 = serverFacade.createGame(new Requests.CreateRequest(log.authToken(), "game"));

        Exception e = Assertions.assertThrows(Exception.class, () -> serverFacade.joinGame(new Requests.JoinRequest("F", "WHITE", g1.gameID())));
        Assertions.assertTrue(e.getMessage().contains("401"));

        //Note that checking if the playerColor is valid occurs in the handler

        Exception e2 = Assertions.assertThrows(Exception.class, () -> serverFacade.joinGame(new Requests.JoinRequest(log.authToken(), "WHITE", 123)));
        Assertions.assertTrue(e2.getMessage().contains("400"));

        serverFacade.joinGame(new Requests.JoinRequest(log.authToken(), "WHITE", g1.gameID()));

        serverFacade.register(new Requests.RegisterRequest("username2", "password2", "email2"));
        Results.LoginResult log2 = serverFacade.login(new Requests.LoginRequest("username2", "password2"));

        Exception e3 = Assertions.assertThrows(Exception.class, () ->
                serverFacade.joinGame(new Requests.JoinRequest(log2.authToken(), "WHITE", g1.gameID())));
        Assertions.assertTrue(e3.getMessage().contains("403"));
    }

    @Test
    @Order(13)
    @DisplayName("Clear Positive")
    public void clearSuccess() {
        serverFacade.register(new Requests.RegisterRequest("username", "password", "email"));
        Results.LoginResult log = serverFacade.login(new Requests.LoginRequest("username", "password"));
        serverFacade.createGame(new Requests.CreateRequest(log.authToken(), "game"));
        Results.ClearResult res = serverFacade.clear();
        Assertions.assertNull(res.message());
    }

}
