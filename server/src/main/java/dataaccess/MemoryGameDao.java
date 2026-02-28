package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;

public class MemoryGameDao implements GameDao{
    public HashMap<Integer, GameData> gamedatabase;

    public MemoryGameDao() {
        gamedatabase = new HashMap<>();
    }

    @Override
    public ArrayList<GameData> getGames() {
        return new ArrayList<GameData>(gamedatabase.values());
    }

    @Override
    public void clear() {
        gamedatabase.clear();
    }
}
