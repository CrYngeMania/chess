package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.HashMap;


public interface DataAccess {
    void clear();
    void saveUser(UserData user) throws DataAccessException;
    UserData getUser(String username);
}
