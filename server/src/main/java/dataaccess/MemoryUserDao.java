package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDao implements UserDao{
    public HashMap<String, UserData> userdatabase;

    @Override
    public UserData getUser(String username) {
        return userdatabase.get(username);
    }

    @Override
    public void createUser(UserData userData) {
        userdatabase.put(userData.username(), userData);
    }
}
