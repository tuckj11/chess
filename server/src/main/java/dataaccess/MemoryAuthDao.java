package dataaccess;

import model.AuthData;
import java.util.HashMap;
import java.util.UUID;


public class MemoryAuthDao implements AuthDao{
    public HashMap<String, AuthData> authdatabase;

    public MemoryAuthDao() {
        authdatabase = new HashMap<>();
    }
    @Override
    public AuthData createAuth(String username) {
        String token = UUID.randomUUID().toString();
        AuthData authData = new AuthData(token, username);
        authdatabase.put(token, authData);
        return authData;
    }

    @Override
    public AuthData verifyAuth(String authToken) {
        return authdatabase.get(authToken);
    }

    @Override
    public void deleteAuth(AuthData authData) {
        AuthData data = authdatabase.remove(authData.authToken());
    }

    @Override
    public void clear() {
        authdatabase.clear();
    }
}
