package service;

import dataaccess.AuthDao;
import dataaccess.GameDao;
import dataaccess.UserDao;
import model.AuthData;
import model.GameData;
import java.util.ArrayList;

public class GameService {
    public record ListRequest(String authToken) {}
    public record ListResult(ArrayList<GameData> games, String message) {}

    public record CreateRequest(String authToken, String gameName) {}
    public record CreateResult(int gameID) {}

    public record JoinRequest(String playerColor, int gameID) {}
    public record JoinResult() {}

    final UserDao userDao;
    final GameDao gameDao;
    final AuthDao authDao;

    public GameService(UserDao userDao, GameDao gameDao, AuthDao authDao) {
        this.userDao = userDao;
        this.gameDao = gameDao;
        this.authDao = authDao;
    }

    public ListResult listGames(ListRequest r) {
        AuthData auth = authDao.verifyAuth(r.authToken());
        if(auth == null){
            return new ListResult(null, "Error: unauthorized");
        }
        else{
            ArrayList<GameData> games = gameDao.getGames();
            return new ListResult(games, null);
        }
    }
}
