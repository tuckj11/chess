package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MemoryGameDao implements GameDao{
    public HashMap<Integer, GameData> gamedatabase;

    public MemoryGameDao() {
        gamedatabase = new HashMap<>();
    }

    @Override
    public ArrayList<GameData> getGames() {
        return new ArrayList<>(gamedatabase.values());
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        throw new DataAccessException("Not implemented");
    }

    @Override
    public Integer makeGame(String gameName) {
        Random rand = new Random();
        int gameID = rand.nextInt(9000) + 1000;
        gamedatabase.put(gameID, new GameData(gameID, null, null, gameName, new ChessGame()));
        return gameID;
    }

    @Override
    public int connectToGame(String username, Integer gameID, String playerColor) {
        GameData game = gamedatabase.get(gameID);
        if(game == null) {
            return 1;
        }
        if(playerColor.equals("WHITE")) {
            if(game.whiteUsername() != null) {
                return 2;
            }
            else {
                gamedatabase.put(gameID, new GameData(gameID, username, game.blackUsername(), game.gameName(), game.game()));
                return 0;
            }
        }
        else {
            if(game.blackUsername() != null) {
                return 2;
            }
            else {
                gamedatabase.put(gameID, new GameData(gameID, game.whiteUsername(), username, game.gameName(), game.game()));
                return 0;
            }
        }
    }

    @Override
    public void clear() {
        gamedatabase.clear();
    }

    public HashMap<Integer, GameData> getdatabase() {
        return this.gamedatabase;
    }
}
