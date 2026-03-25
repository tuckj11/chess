package service;


import dataaccess.AuthDao;
import dataaccess.DataAccessException;
import dataaccess.GameDao;
import dataaccess.UserDao;
import results.Results;

public class ClearService {

    final UserDao userDao;
    final GameDao gameDao;
    final AuthDao authDao;

    public ClearService(UserDao userDao, GameDao gameDao, AuthDao authDao) {
        this.userDao = userDao;
        this.gameDao = gameDao;
        this.authDao = authDao;
    }

    public Results.ClearResult clear() {
        try {
            userDao.clear();
            gameDao.clear();
            authDao.clear();
            return new Results.ClearResult(null);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
