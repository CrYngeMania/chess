package dataaccess;

import model.GameData;

import java.util.ArrayList;

public interface GameDataAccess {
    void saveGame(GameData game) throws DataAccessException;
    GameData getGame(Integer gameID) throws DataAccessException;
    ArrayList<GameData> getGamesList() throws DataAccessException;
    void updateGame(Integer gameID, GameData newGame) throws DataAccessException;
    void clear() throws DataAccessException;
}
