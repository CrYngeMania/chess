package client;

import dataaccess.DataAccessException;
import datamodel.*;
import facade.ServerFacade;
import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static String baseUrl;

    private ServerFacade facade = new ServerFacade(baseUrl);

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        baseUrl = "http://localhost:" + port;
        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    void setup() throws Exception {
        facade.delete();
        facade.register(new RegistrationRequest("dippleDop", "impy", "sv"));
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void testRegisterPass() throws DataAccessException {
        RegistrationRequest request = new RegistrationRequest("Jackaboy", "wapoosh", "ksdjhf");
        RegistrationResult result = facade.register(request);

        assertEquals(result.username(), request.username());
        assertNotNull(result.authToken());
    }

    @Test
    public void testRegisterFail() throws DataAccessException {
        RegistrationRequest request = new RegistrationRequest(null, "wapoosh", "ksdjhf");

        assertThrows(DataAccessException.class, () -> facade.register(request));
    }

    @Test
    public void testLoginPass() throws DataAccessException {
        LoginRequest request = new LoginRequest("dippleDop", "impy");
        LoginResult result = facade.login(request);

        assertEquals(result.username(), request.username());
        assertNotNull(result.authToken());
    }

    @Test
    public void testLoginFail() throws DataAccessException {
        LoginRequest request = new LoginRequest("illFail", "watch me fail");
        assertThrows(DataAccessException.class, () -> facade.login(request));
    }

    @Test
    public void testLogoutSuccess() throws DataAccessException {
        LoginRequest request = new LoginRequest("dippleDop", "impy");
        facade.login(request);

        LogoutResult result = facade.logout();
        assertNotNull(result);
    }

    @Test
    public void testLogoutFail() throws DataAccessException {
        facade.delete();
        assertThrows(DataAccessException.class, () -> facade.logout());
    }

    @Test
    public void testCreateGamePass() throws DataAccessException {
        CreateGameRequest request = new CreateGameRequest("creative name");
        CreateGameResult result = facade.createGame(request);

        assertNotNull(result.gameID());
    }

    @Test
    public void testCreateGameFail() throws DataAccessException{
        CreateGameRequest request = new CreateGameRequest(null);
        assertThrows(DataAccessException.class, () -> facade.createGame(request));

        facade.delete();
        CreateGameRequest request2 = new CreateGameRequest("creative name");
        assertThrows(DataAccessException.class, () -> facade.createGame(request2));
    }

    @Test
    public void testListGamePass() throws DataAccessException{
        CreateGameRequest request = new CreateGameRequest("creative name");
        facade.createGame(request);

        assertNotNull(facade.listGame());

        CreateGameRequest request2 = new CreateGameRequest("another creative name");
        facade.createGame(request2);
        CreateGameRequest request3 = new CreateGameRequest("im so creative");
        facade.createGame(request3);

        assertNotNull(facade.listGame());
    }

    @Test
    public void testListGameFail() throws DataAccessException{
        facade.delete();

        assertThrows(DataAccessException.class, () -> facade.listGame());
    }

    @Test
    public void testJoinGamePass() throws DataAccessException {
        CreateGameRequest gameRequest = new CreateGameRequest("creative name");
        CreateGameResult gameResult = facade.createGame(gameRequest);
        JoinGameRequest request = new JoinGameRequest("WHITE", gameResult.gameID());

        assertDoesNotThrow(() -> facade.joinGame(request));
    }

    @Test
    public void testJoinGameFail() throws DataAccessException {
        CreateGameRequest gameRequest = new CreateGameRequest("creative name");
        CreateGameResult gameResult = facade.createGame(gameRequest);
        JoinGameRequest request = new JoinGameRequest("WHITE", gameResult.gameID());
        facade.joinGame(request);
        JoinGameRequest request2 = new JoinGameRequest("WHITE", gameResult.gameID());
        assertThrows(DataAccessException.class, () -> facade.joinGame(request2));
    }

    @Test
    public void testDeletePass() throws DataAccessException {
        CreateGameRequest gameRequest = new CreateGameRequest("creative name");
        facade.createGame(gameRequest);

        CreateGameRequest request2 = new CreateGameRequest("another creative name");
        facade.createGame(request2);
        CreateGameRequest request3 = new CreateGameRequest("im so creative");
        facade.createGame(request3);

        facade.delete();

        assertThrows(DataAccessException.class, () -> facade.login(new LoginRequest("dippleDop", "impy")));
    }
}
