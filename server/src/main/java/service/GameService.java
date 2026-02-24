package service;

import model.GameData;
import java.util.ArrayList;

public class GameService {
    public record ListRequest(String authToken) {}
    public record ListResult(ArrayList<GameData> games) {}

    public record CreateRequest(String authToken, String gameName) {}
    public record CreateResult(int gameID) {}

    public record JoinRequest(String playerColor, int gameID) {}
    public record JoinResult() {}
}
