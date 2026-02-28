package dataaccess;

import model.GameData;

import java.util.ArrayList;

public interface GameDao {
    public ArrayList<GameData> getGames();
    public Integer makeGame(String gameName);
    public int connectToGame(String username, Integer gameID, String playerColor);
    public void clear();
}
