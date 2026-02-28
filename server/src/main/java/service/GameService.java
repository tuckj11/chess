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
    public record CreateResult(Integer gameID, String message) {}

    public record JoinRequest(String authToken, String playerColor, Integer gameID) {}
    public record JoinResult(String message) {}

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

    public CreateResult createGame(CreateRequest r) {
        AuthData auth = authDao.verifyAuth(r.authToken());
        if(auth == null){
            return new CreateResult(null, "Error: unauthorized");
        }
        else {
            Integer gameID = gameDao.makeGame(r.gameName());
            return new CreateResult(gameID, null);
        }
    }

    public JoinResult joinGame(JoinRequest r) {
        AuthData auth = authDao.verifyAuth(r.authToken());
        if(auth == null){
            return new JoinResult("Error: unauthorized");
        }
        int successfulJoin = gameDao.connectToGame(auth.username(), r.gameID(), r.playerColor());
        if(successfulJoin == 0) {
            return new JoinResult(null);
        }
        else if(successfulJoin == 1) {
            return new JoinResult("Error: Bad Request");
        }
        else {
            return new JoinResult("Error: Already Taken");
        }
    }

    public CreateRequest addAuthToCreateRequest(String auth, CreateRequest r) {
        return new CreateRequest(auth, r.gameName());
    }

    public JoinRequest addAuthToJoinRequest(String auth, JoinRequest r) {
        return new JoinRequest(auth, r.playerColor(), r.gameID());
    }
}
