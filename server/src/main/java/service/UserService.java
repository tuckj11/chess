package service;

import dataaccess.AuthDao;
import dataaccess.DataAccessException;
import dataaccess.UserDao;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;


public class UserService {
    public record RegisterRequest(String username, String password, String email) {}
    public record RegisterResult(String username, String authToken, String message) {}

    public record LoginRequest(String username, String password) {}
    public record LoginResult(String username, String authToken, String message) {}

    public record LogoutRequest(String authToken) {}
    public record LogoutResult(String message) {}

    final UserDao userDao;
    final AuthDao authDao;
    public UserService(UserDao userDao, AuthDao authDao) {
        this.userDao = userDao;
        this.authDao = authDao;
    }

    public RegisterResult register(RegisterRequest r)  {
        try {
            UserData user = userDao.getUser(r.username());
            if (user != null) {
                return new RegisterResult(null, null, "Error: already taken");
            }
            String password = BCrypt.hashpw(r.password(), BCrypt.gensalt());
            userDao.createUser(new UserData(r.username(), password, r.email()));
            AuthData authData = authDao.createAuth(r.username());
            return new RegisterResult(authData.username(), authData.authToken(), null);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public LoginResult login(LoginRequest r) {
        try {
            UserData user = userDao.getUser(r.username());
            if (user == null) {
                return new LoginResult(null, null, "Error: unauthorized");
            }
            if (!BCrypt.checkpw(r.password(), user.password())) {
                System.out.println(user.password());
                return new LoginResult(null, null, "Error: unauthorized");
            }
            AuthData authData = authDao.createAuth(r.username());
            return new LoginResult(authData.username(), authData.authToken(), null);
        }
        catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public LogoutResult logout(LogoutRequest r) {
        try {
            AuthData auth = authDao.verifyAuth(r.authToken());
            if (auth == null) {
                return new LogoutResult("Error: unauthorized");
            }
            authDao.deleteAuth(auth);
            return new LogoutResult(null);
        }
        catch (DataAccessException e){
            throw new RuntimeException(e);
        }
    }

}
