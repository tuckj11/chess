package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;

public interface GameDao {
    ArrayList<GameData> getGames() throws DataAccessException;
    GameData getGame(int GameID) throws DataAccessException;
    Integer makeGame(String gameName) throws DataAccessException;

    void updateColorData(int gameID, String color) throws DataAccessException;
    void updateGame(int gameID, ChessGame game) throws DataAccessException;
    int connectToGame(String username, Integer gameID, String playerColor) throws DataAccessException;
    void clear() throws DataAccessException;
}
