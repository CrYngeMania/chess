package dataaccess;

import model.GameData;

import java.util.ArrayList;

public interface GameDataAccess {
    void saveGame(GameData game);
    GameData getGame(Integer gameID);
    ArrayList<GameData> getGamesList();
    void updateGame(Integer gameID, GameData newGame);
    void clear();
}
