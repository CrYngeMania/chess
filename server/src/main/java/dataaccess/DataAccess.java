package dataaccess;

import model.UserData;

public interface DataAccess {
    void clear();
    void saveUser(UserData user);
    UserData getUser(String username);
}
