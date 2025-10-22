package service;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDataAccess;
import dataaccess.MemoryDataAccess;
import dataaccess.MemoryGameDataAccess;
import datamodel.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {
    static MemoryGameDataAccess memoryGameDA  = new MemoryGameDataAccess();
    static MemoryAuthDataAccess memoryAuthDA = new MemoryAuthDataAccess();
    static final GameService GAME_SERVICE = new GameService(memoryGameDA, memoryAuthDA);
    static final UserService USER_SERVICE = new UserService(new MemoryDataAccess(), memoryGameDA, memoryAuthDA);
    RegistrationRequest user = new RegistrationRequest("goodfarmswithscar", "well hello there", "struggling rn");
    RegistrationResult registrationResult;
    String existingAuth;
    CreateGameRequest createRequest = new CreateGameRequest("howdy");
    CreateGameResult createGame;


    @BeforeEach

    void clear() throws DataAccessException {
        USER_SERVICE.delete(null);
    }

    public void initRegister() throws DataAccessException {
        registrationResult = USER_SERVICE.register(user);
        existingAuth = registrationResult.authToken();
        createGame = GAME_SERVICE.createGame(createRequest, existingAuth);
    }

    @Test
    public void createGamePass() throws DataAccessException {
        initRegister();
        CreateGameRequest createRequest = new CreateGameRequest("Speedrunning");
        CreateGameResult createGameResult = GAME_SERVICE.createGame(createRequest, existingAuth);
        assertNotNull(createGameResult.gameID());

    }

    @Test
    public void createFail() throws DataAccessException {
        initRegister();
        CreateGameRequest createRequest = new CreateGameRequest("hi");
        assertThrows(DataAccessException.class, () -> GAME_SERVICE.createGame(createRequest, null));
        CreateGameRequest createNoName = new CreateGameRequest(null);
        assertThrows(DataAccessException.class, () -> GAME_SERVICE.createGame(createNoName, existingAuth));
    }

    @Test
    public void listGamesPass() throws DataAccessException {
        initRegister();
        CreateGameRequest createRequest1 = new CreateGameRequest("hi");
        GAME_SERVICE.createGame(createRequest1, existingAuth);
        CreateGameRequest createRequest2 = new CreateGameRequest("hello");
        GAME_SERVICE.createGame(createRequest2, existingAuth);
        CreateGameRequest createRequest3 = new CreateGameRequest("howdy");
        GAME_SERVICE.createGame(createRequest3, existingAuth);


        ListGameResult listGamesResult = GAME_SERVICE.listGame(existingAuth);
        assertNotNull(listGamesResult);

    }

    @Test
    public void listGamesFail() throws DataAccessException {
        initRegister();
        assertThrows(DataAccessException.class, () -> GAME_SERVICE.listGame(null));
    }

    @Test
    public void joinGamePass() throws DataAccessException {
        initRegister();
        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", createGame.gameID());
        assertDoesNotThrow(() -> GAME_SERVICE.joinGame(joinGameRequest, existingAuth));
        JoinGameRequest joinGameRequest2 = new JoinGameRequest("BLACK", createGame.gameID());
        assertDoesNotThrow(() -> GAME_SERVICE.joinGame(joinGameRequest2, existingAuth));

    }

    @Test
    public void joinGameFail() throws DataAccessException {
        initRegister();
        JoinGameResult joinGameResult = GAME_SERVICE.joinGame(new JoinGameRequest("WHITE", createGame.gameID()), existingAuth);
        assertThrows(DataAccessException.class, () -> GAME_SERVICE.joinGame(new JoinGameRequest("WHITE", createGame.gameID()), existingAuth));

        assertThrows(DataAccessException.class, () -> GAME_SERVICE.joinGame(new JoinGameRequest("ORANGE", createGame.gameID()), existingAuth));
    }
}
