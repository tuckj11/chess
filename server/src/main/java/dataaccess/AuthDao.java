package dataaccess;

import model.AuthData;

public interface AuthDao {
    public AuthData createAuth(String username);
}
