package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.HashMap;


public interface DataAccess {
    void clear();
    void saveUser(UserData user);
    UserData getUser(String username);
    AuthData getAuth(String token);
    void deleteAuth(AuthData auth);
    void saveAuth(AuthData auth);
    HashMap<String, AuthData> getAuths();
}
