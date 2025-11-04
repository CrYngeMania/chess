package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.HashMap;


public interface DataAccess {
    void clear() throws DataAccessException;
    void saveUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
}
