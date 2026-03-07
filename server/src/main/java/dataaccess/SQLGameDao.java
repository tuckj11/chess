package dataaccess;

import model.GameData;

import java.util.ArrayList;

public class SQLGameDao implements GameDao{
    public SQLGameDao() {

    }

    @Override
    public ArrayList<GameData> getGames() {
        return null;
    }

    @Override
    public Integer makeGame(String gameName) {
        return 0;
    }

    @Override
    public int connectToGame(String username, Integer gameID, String playerColor) {
        return 0;
    }

    @Override
    public void clear() {

    }
}
