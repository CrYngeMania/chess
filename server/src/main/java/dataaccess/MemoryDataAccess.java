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


    public MemoryDataAccess(){
    }

    @Override
    public void clear(){
        users.clear();
    }


    @Override
    public void saveUser(UserData user) {
        users.put(user.username(), user);
    }



    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

}
