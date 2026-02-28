package dataaccess;

import model.AuthData;

public interface AuthDao {
    public AuthData createAuth(String username);
    public AuthData verifyAuth(String authToken);
    public void deleteAuth(AuthData authData);
    public void clear();
}
