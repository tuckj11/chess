package client;

import com.google.gson.Gson;
import requests.Requests;
import results.Results;

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


    public Results.RegisterResult register(Requests.RegisterRequest r) {
        return makeRequest("POST", "/user", r, null, Results.RegisterResult.class);
    }

    public Results.LoginResult login(Requests.LoginRequest r) {
        return makeRequest("POST", "/session", r, null, Results.LoginResult.class);
    }

    public Results.LogoutResult logout(Requests.LogoutRequest r) {
        return makeRequest("DELETE", "/session", r, r.authToken(), Results.LogoutResult.class);
    }

    public Results.ListResult listGames(Requests.ListRequest r) {
        return makeRequest("GET", "/game", null, r.authToken(), Results.ListResult.class);
    }

    public Results.CreateResult createGame(Requests.CreateRequest r) {
        return makeRequest("POST", "/game", r,r.authToken(), Results.CreateResult.class);
    }

    public Results.JoinResult joinGame(Requests.JoinRequest r) {
        return makeRequest("PUT", "/game", r, r.authToken(), Results.JoinResult.class);

    }

    public Results.ClearResult clear() {
        return makeRequest("DELETE", "/db", null, null, Results.ClearResult.class);

    }

    private <T> T makeRequest(String method, String path, Object request, String authToken, Class<T> responseClass) {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (authToken != null) {
                http.setRequestProperty("Authorization", authToken);  // ← add this
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);

        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
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

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: " + status);
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
