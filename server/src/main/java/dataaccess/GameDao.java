package dataaccess;

import model.GameData;

import java.util.ArrayList;

public interface GameDao {
    public ArrayList<GameData> getGames();
    public void clear();
}
