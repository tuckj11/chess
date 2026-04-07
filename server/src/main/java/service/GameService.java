package service;

import chess.ChessGame;
import dataaccess.AuthDao;
import dataaccess.DataAccessException;
import dataaccess.GameDao;
import dataaccess.UserDao;
import model.AuthData;
import model.GameData;
import requests.Requests;
import results.Results;

import java.util.ArrayList;

public class GameService {



    final UserDao userDao;
    final GameDao gameDao;
    final AuthDao authDao;

    public GameService(UserDao userDao, GameDao gameDao, AuthDao authDao) {
        this.userDao = userDao;
        this.gameDao = gameDao;
        this.authDao = authDao;
    }

    public Results.ListResult listGames(Requests.ListRequest r) {
        try {
            AuthData auth = authDao.verifyAuth(r.authToken());
            if (auth == null) {
                return new Results.ListResult(null, "Error: unauthorized");
            } else {
                ArrayList<GameData> games = gameDao.getGames();
                return new Results.ListResult(games, null);
            }
        }
        catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Results.CreateResult createGame(Requests.CreateRequest r) {
        try {
            AuthData auth = authDao.verifyAuth(r.authToken());
            if (auth == null) {
                return new Results.CreateResult(null, "Error: unauthorized");
            } else {
                Integer gameID = gameDao.makeGame(r.gameName());
                return new Results.CreateResult(gameID, null);
            }
        }
        catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Results.JoinResult joinGame(Requests.JoinRequest r) {
        try {
            AuthData auth = authDao.verifyAuth(r.authToken());
            if (auth == null) {
                return new Results.JoinResult("Error: unauthorized");
            }
            int successfulJoin = gameDao.connectToGame(auth.username(), r.gameID(), r.playerColor());
            if (successfulJoin == 0) {
                return new Results.JoinResult(null);
            } else if (successfulJoin == 1) {
                return new Results.JoinResult("Error: Bad Request");
            } else {
                return new Results.JoinResult("Error: Already Taken");
            }
        }
        catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public GameData getGame(int gameID) {
        try {
            return gameDao.getGame(gameID);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateGameColor(int gameID, String color) {
        try {
            gameDao.updateColorData(gameID, color);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void makeMove(int gameID, ChessGame game) {
        try {
            gameDao.updateGame(gameID, game);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Requests.CreateRequest addAuthToCreateRequest(String auth, Requests.CreateRequest r) {
        return new Requests.CreateRequest(auth, r.gameName());
    }

    public Requests.JoinRequest addAuthToJoinRequest(String auth, Requests.JoinRequest r) {
        return new Requests.JoinRequest(auth, r.playerColor(), r.gameID());
    }
}
