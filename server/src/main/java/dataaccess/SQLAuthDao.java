package dataaccess;

import model.AuthData;

public class SQLAuthDao implements AuthDao{
    @Override
    public AuthData createAuth(String username) {
        return null;
    }

    @Override
    public AuthData verifyAuth(String authToken) {
        return null;
    }

    @Override
    public void deleteAuth(AuthData authData) {

    }

    @Override
    public void clear() {

    }
}
