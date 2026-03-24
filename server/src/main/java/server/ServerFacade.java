package server;

import service.UserService;
import service.GameService;
import service.ClearService;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;

public class ServerFacade {
    //private static final HttpClient httpClient = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        this.serverUrl = url;
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

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws Exception {
        URL url = (new URI(serverUrl + path)).toURL();
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod(method);
        http.setDoOutput(true);
    }
}
