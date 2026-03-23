package server;

import service.UserService;
import service.GameService;
import service.ClearService;

import java.net.http.HttpClient;

public class ServerFacade {
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public static void main(String[] args) {

    }

    public UserService.RegisterResult register(UserService.RegisterRequest r) {

    }

    public UserService.LoginResult login(UserService.LoginRequest r) {

    }

    public UserService.LogoutResult logout(UserService.LogoutRequest r) {

    }

    public GameService.ListResult listGames(GameService.ListRequest r) {

    }

    public GameService.CreateResult createGame(GameService.CreateRequest r) {

    }

    public GameService.JoinResult joinGame(GameService.JoinRequest r) {

    }

    public ClearService.ClearResult clearGame() {

    }

}
