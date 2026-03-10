package dataaccess;

import model.GameData;

import java.util.ArrayList;

public interface GameDao {
    public ArrayList<GameData> getGames() throws DataAccessException;
    public Integer makeGame(String gameName) throws DataAccessException;
    public int connectToGame(String username, Integer gameID, String playerColor) throws DataAccessException;
    public void clear() throws DataAccessException;
}
