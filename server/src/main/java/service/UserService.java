package service;

import dataaccess.AuthDao;
import dataaccess.DataAccessException;
import dataaccess.UserDao;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import requests.Requests;
import results.Results;


public class UserService {
    final UserDao userDao;
    final AuthDao authDao;
    public UserService(UserDao userDao, AuthDao authDao) {
        this.userDao = userDao;
        this.authDao = authDao;
    }

    public Results.RegisterResult register(Requests.RegisterRequest r)  {
        try {
            UserData user = userDao.getUser(r.username());
            if (user != null) {
                return new Results.RegisterResult(null, null, "Error: already taken");
            }
            String password = BCrypt.hashpw(r.password(), BCrypt.gensalt());
            userDao.createUser(new UserData(r.username(), password, r.email()));
            AuthData authData = authDao.createAuth(r.username());
            return new Results.RegisterResult(authData.username(), authData.authToken(), null);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Results.LoginResult login(Requests.LoginRequest r) {
        try {
            UserData user = userDao.getUser(r.username());
            if (user == null) {
                return new Results.LoginResult(null, null, "Error: unauthorized");
            }
            if (!BCrypt.checkpw(r.password(), user.password())) {
                System.out.println(user.password());
                return new Results.LoginResult(null, null, "Error: unauthorized");
            }
            AuthData authData = authDao.createAuth(r.username());
            return new Results.LoginResult(authData.username(), authData.authToken(), null);
        }
        catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Results.LogoutResult logout(Requests.LogoutRequest r) {
        try {
            AuthData auth = authDao.verifyAuth(r.authToken());
            if (auth == null) {
                return new Results.LogoutResult("Error: unauthorized");
            }
            authDao.deleteAuth(auth);
            return new Results.LogoutResult(null);
        }
        catch (DataAccessException e){
            throw new RuntimeException(e);
        }
    }

    public Boolean verifyAuth(String authToken) {
        try {
            AuthData auth = authDao.verifyAuth(authToken);
            return auth != null;
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
