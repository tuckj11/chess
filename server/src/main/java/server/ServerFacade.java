package server;

import com.google.gson.Gson;
import io.javalin.http.HttpResponseException;
import service.UserService;
import service.GameService;
import service.ClearService;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String url) {
        this.serverUrl = url;
    }


    public UserService.RegisterResult register(UserService.RegisterRequest r) {
        return makeRequest("POST", "/user", r, UserService.RegisterResult.class);
    }

    public UserService.LoginResult login(UserService.LoginRequest r) {
        return makeRequest("POST", "/session", r, UserService.LoginResult.class);
    }

    public UserService.LogoutResult logout(UserService.LogoutRequest r) {
        return makeRequest("DELETE", "/session", r, UserService.LogoutResult.class);
    }

    public GameService.ListResult listGames(GameService.ListRequest r) {
        return makeRequest("GET", "/game", r, GameService.ListResult.class);
    }

    public GameService.CreateResult createGame(GameService.CreateRequest r) {
        return makeRequest("POST", "/game", r, GameService.CreateResult.class);
    }

    public GameService.JoinResult joinGame(GameService.JoinRequest r) {
        return makeRequest("PUT", "/game", r, GameService.JoinResult.class);

    }

    public ClearService.ClearResult clear() {
        return makeRequest("DELETE", "/db", null, ClearService.ClearResult.class);

    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws HttpResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);

        } catch (Exception e) {
            throw new HttpResponseException(500, e.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, HttpResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new HttpResponseException(status, "failure: " + status);
        }

    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() != 0) {
            try(InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if(responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
