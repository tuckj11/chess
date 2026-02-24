package service;

import model.GameData;

import java.util.ArrayList;

record ListRequest(String authToken) {}
record ListResult(ArrayList<GameData> games) {}

record CreateRequest(String authToken, String gameName) {}
record CreateResult(int gameID) {}

record JoinRequest(String playerColor, int gameID) {}
record JoinResult() {}

public class GameService {
}
