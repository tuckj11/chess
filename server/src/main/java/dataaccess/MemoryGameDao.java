package dataaccess;

import model.GameData;

import java.util.HashMap;

public class MemoryGameDao implements GameDao{
    public HashMap<String, GameData> gamedatabase;

    public MemoryGameDao() {
        gamedatabase = new HashMap<>();
    }

    @Override
    public void clear() {
        gamedatabase.clear();
    }
}
