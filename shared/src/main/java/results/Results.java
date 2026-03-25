package results;

import model.GameData;

import java.util.ArrayList;

public class Results {
    public record ClearResult(String message) {}
    public record ListResult(ArrayList<GameData> games, String message) {}
    public record CreateResult(Integer gameID, String message) {}
    public record JoinResult(String message) {}
    public record RegisterResult(String username, String authToken, String message) {}
    public record LoginResult(String username, String authToken, String message) {}
    public record LogoutResult(String message) {}

}
