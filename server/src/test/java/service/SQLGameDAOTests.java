package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MySqlGameDataAccess;
import dataaccess.MySqlUserDataAccess;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SQLGameDAOTests {

    private final MySqlUserDataAccess userAccess = new MySqlUserDataAccess();
    private final MySqlGameDataAccess gameAccess = new MySqlGameDataAccess();

    @BeforeEach
    void setup() throws Exception {
        userAccess.configureDatabase();
        gameAccess.clear();
    }

    @Test
    public void clearSuccess() throws DataAccessException {
        ChessGame chess = new ChessGame();
        GameData game = new GameData(17, null, null, null, chess);
        gameAccess.saveGame(game);

        gameAccess.clear();
        assertNull(gameAccess.getGame(game.gameID()));
    }

    @Test
    public void saveGameSuccess() throws DataAccessException {
        ChessGame chess = new ChessGame();
        GameData game = new GameData(17, null, null, null, chess);
        gameAccess.saveGame(game);

        var result = gameAccess.getGame(game.gameID());
        assertNotNull(result);
        assertEquals(game.gameID(), result.gameID());
    }

    @Test
    public void saveGameFail() throws DataAccessException {
        ChessGame chess = new ChessGame();
        GameData game = new GameData(21, null, null, null, chess);
        gameAccess.saveGame(game);

        GameData game2 = new GameData(21, null, null, null, chess);
        assertThrows(DataAccessException.class, () -> gameAccess.saveGame(game2));
    }

    @Test
    public void getGameSuccess() throws DataAccessException {
        ChessGame chess = new ChessGame();
        GameData game = new GameData(17, "Goodtimes", "TheDiggity", "ScarredDogs", chess);
        gameAccess.saveGame(game);

        GameData game2 = new GameData(21, "Mumbo Jumbolio", "Pesky bird", "Mumbot", chess);
        gameAccess.saveGame(game2);

        var result1 = gameAccess.getGame(game.gameID());
        var result2 = gameAccess.getGame(game2.gameID());
        assertNotNull(result1);
        assertEquals(game.gameID(), result1.gameID());
        assertEquals(game.whiteUsername(), result1.whiteUsername());

        assertNotNull(result2);
        assertEquals(game2.gameID(), result2.gameID());
        assertEquals(game2.whiteUsername(), result2.whiteUsername());
    }

    @Test
    public void getGameFail() throws DataAccessException {
        assertNull(gameAccess.getGame(420));
    }

    @Test
    public void getGameListSuccess() throws DataAccessException {
        ChessGame chess = new ChessGame();
        GameData game = new GameData(1, "Goodtimes", "TheDiggity", "ScarredDogs", chess);
        gameAccess.saveGame(game);

        GameData game2 = new GameData(2, "Mumbo Jumbolio", "Pesky bird", "Mumbot", chess);
        gameAccess.saveGame(game2);

        GameData game3 = new GameData(3, "Beans", "Littlewood", "funny last names", chess);
        gameAccess.saveGame(game3);

        GameData game4 = new GameData(5, "Dipple Dop", "Skizzlebizzle", "arizona dads", chess);
        gameAccess.saveGame(game4);

        var result = gameAccess.getGamesList();
        assertEquals(4, result.size());
    }

    @Test
    public void updateGameSuccess() throws DataAccessException {
        ChessGame chess = new ChessGame();
        GameData game = new GameData(1, "Goodtimes", null, "ScarredDogs", chess);
        gameAccess.saveGame(game);
        GameData game2 = new GameData(1, "Goodtimes", "The Diggity", "ScarredDogs", chess);

        gameAccess.updateGame(game.gameID(), game2);

        var result = gameAccess.getGame(game.gameID());
        assertEquals(game.gameID(), result.gameID());
        assertEquals(game2.blackUsername(), result.blackUsername());
        assertNotEquals(game.blackUsername(), result.blackUsername());
    }

    @Test
    public void updateGameFail() throws DataAccessException {
        ChessGame chess = new ChessGame();
        GameData game = new GameData(1, "Goodtimes", null, "ScarredDogs", chess);
        gameAccess.updateGame(420, game);
        var result = gameAccess.getGame(game.gameID());
        assertNull(result);


    }
}
