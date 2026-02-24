package service;

import dataaccess.AuthDao;
import dataaccess.UserDao;
import model.UserData;

public class UserService {
    public record RegisterRequest(String username, String password, String email) {}
    public record RegisterResult(String username, String authToken, String message) {}

    public record LoginRequest(String username, String password) {}
    public record LoginResult(String username, String authToken) {}

    public record LogoutRequest(String authToken) {}
    public record LogoutResult() {}

    final UserDao userDao;
    final AuthDao authDao;
    public UserService(UserDao userDao, AuthDao authDao) {
        this.userDao = userDao;
        this.authDao = authDao;
    }

    public RegisterResult register(RegisterRequest r) {
        UserData user = userDao.getUser(r.username());
        if(user == null) {
            userDao.createUser(new UserData(r.username(), r.password(), r.email()));
        }
        return null;
    }

}
