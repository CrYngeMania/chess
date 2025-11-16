package dataaccess;

import exception.ResponseException;
import model.UserData;
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

    @Override
    public boolean verifyUser(String username, String providedPassword) {
        UserData user = getUser(username);
        if (user == null){
            return false;
        }
        String checkPassword = user.password();
        return providedPassword.equals(checkPassword);
    }

}
