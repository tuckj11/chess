package dataaccess;

import model.UserData;

public interface UserDao {
    public UserData getUser(String username) throws DataAccessException;
    public void createUser(UserData userData);
    public void clear();
}
