package dataaccess;

import model.AuthData;

public interface AuthDao {
    public AuthData createAuth(String username) throws DataAccessException;
    public AuthData verifyAuth(String authToken) throws DataAccessException;
    public void deleteAuth(AuthData authData) throws DataAccessException;
    public void clear() throws DataAccessException;
}
