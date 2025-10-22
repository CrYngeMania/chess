package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;

public class MemoryGameDataAccess implements GameDataAccess{
    private HashMap<Integer, GameData> games = new HashMap<>();

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

    @Override
    public void updateGame(Integer gameID, GameData newGame){
        games.put(gameID, newGame);

    }

    @Override
    public void clear(){
        games.clear();
    }
}
