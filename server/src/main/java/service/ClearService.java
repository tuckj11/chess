package service;


import dataaccess.AuthDao;
import dataaccess.DataAccessException;
import dataaccess.GameDao;
import dataaccess.UserDao;

public class ClearService {
    public record ClearResult(String message) {}

    final UserDao userDao;
    final GameDao gameDao;
    final AuthDao authDao;

    public ClearService(UserDao userDao, GameDao gameDao, AuthDao authDao) {
        this.userDao = userDao;
        this.gameDao = gameDao;
        this.authDao = authDao;
    }

    public ClearResult clear() {
        try {
            userDao.clear();
            gameDao.clear();
            authDao.clear();
            return new ClearResult(null);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
