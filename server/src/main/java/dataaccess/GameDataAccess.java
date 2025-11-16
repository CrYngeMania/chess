package dataaccess;

import exception.ResponseException;
import model.GameData;

import java.util.ArrayList;

public interface GameDataAccess {
    void saveGame(GameData game) throws ResponseException;
    GameData getGame(Integer gameID) throws ResponseException;
    ArrayList<GameData> getGamesList() throws ResponseException;
    void updateGame(Integer gameID, GameData newGame) throws ResponseException;
    void clear() throws ResponseException;
}
