package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MySqlGameDataAccess implements GameDataAccess{

    MySqlDatabaseHandler handler = new MySqlDatabaseHandler();

    @Override
    public void saveGame(GameData game) throws DataAccessException {
        var statement = "INSERT INTO games (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
        var serialiser = new Gson();
        String jsonGame = serialiser.toJson(game.game());
        handler.executeQuery(statement, game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), jsonGame);
    }

    @Override
    public GameData getGame(Integer gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setObject(0, gameID);
                try (ResultSet result = ps.executeQuery()) {
                    if (result.next()) {
                        return readGame(result);
                    }
                }
            }
        }catch (SQLException e) {
            throw new DataAccessException("Error: Database error", e);
        }
        return null;
    }

    private GameData readGame(ResultSet result) throws DataAccessException {
        try {
            var serialiser = new Gson();
            int gameID = result.getInt("gameID");
            String whiteUsername = result.getString("whiteUsername");
            String blackUsername = result.getString("blackUsername");
            String gameName = result.getString("whiteUsername");
            String jsonGame = result.getString("game");
            ChessGame game = serialiser.fromJson(jsonGame, ChessGame.class);
            return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
        }catch(SQLException ex) {
            throw new DataAccessException(DataAccessException.Code.ServerError, "Error:");
        }

    }

    @Override
    public ArrayList<GameData> getGamesList() throws DataAccessException {
        ArrayList<GameData> games = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM games";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet result = ps.executeQuery()) {
                    while (result.next()) {
                        games.add(readGame(result));
                    }
                }
            }
        } catch (Exception e){
            throw new DataAccessException(DataAccessException.Code.ServerError, "Error: Unable to access database");
        }
        return games;
    }

    @Override
    public void updateGame(Integer gameID, GameData newGame) throws DataAccessException {
        var statement = "UPDATE games SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? WHERE gameID = ?";
        var serialiser = new Gson();
        String jsonGame = serialiser.toJson(newGame.game());
        handler.executeQuery(statement, newGame.whiteUsername(), newGame.blackUsername(), newGame.gameName(), jsonGame, newGame.gameID());
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE games";
        handler.executeQuery(statement);
    }
}
