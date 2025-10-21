package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.HashMap;


public class MemoryDataAccess implements DataAccess {

    private HashMap<String, UserData> users = new HashMap<>();
    private HashMap<String, AuthData> auths = new HashMap<>();
    private HashMap<Integer, GameData> games = new HashMap<>();
    private AuthData currAuth;

    @Override
    public void clear(){

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

    @Override
    public void setCurrAuth(AuthData auth) {
        currAuth = auth;
    }

    @Override
    public AuthData getCurrAuth(){return currAuth;
    };

    @Override
    public void saveGame(GameData game) {
        games.put(game.gameID(), game);
    }

    @Override
    public GameData getGame(Integer gameID){
        return games.get(gameID);
    }

    @Override
    public ArrayList<GameData> getGamesList() {
        return new ArrayList<>(games.values());
    }

}
