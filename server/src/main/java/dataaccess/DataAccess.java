package dataaccess;

import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.HashMap;


public interface DataAccess {
    void clear() throws ResponseException;
    void saveUser(UserData user) throws ResponseException;
    UserData getUser(String username) throws ResponseException;
    boolean verifyUser(String username, String providedPassword) throws ResponseException;
}
