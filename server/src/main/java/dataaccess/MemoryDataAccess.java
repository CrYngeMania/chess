package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import dataaccess.GameDataAccess;
import java.util.ArrayList;
import java.util.HashMap;


public class MemoryDataAccess implements DataAccess {
    private GameDataAccess gameDataAccess;

    private HashMap<String, UserData> users = new HashMap<>();
    private HashMap<String, AuthData> auths = new HashMap<>();

    public MemoryDataAccess(){
    }

    @Override
    public void clear(){
        users.clear();
        auths.clear();
    }
    public HashMap<String, AuthData> getAuths(){
        return auths;
    }

    @Override
    public void saveUser(UserData user) {
        users.put(user.username(), user);
    }
    @Override
    public void saveAuth(AuthData auth) {
        auths.put(auth.authToken(), auth);
    }
    @Override
    public void deleteAuth(AuthData auth) {
        auths.remove(auth.authToken());
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public AuthData getAuth(String token) {
        return auths.get(token);
    }

}
