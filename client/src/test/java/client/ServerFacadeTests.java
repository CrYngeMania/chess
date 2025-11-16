package client;

import dataaccess.DataAccessException;
import datamodel.*;
import facade.ServerFacade;
import org.junit.jupiter.api.*;
import server.Server;

import java.util.HashMap;

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
        facade.register("dippleDop", "impy", "sv");
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void testRegisterPass() throws DataAccessException {
        HashMap<String, Object> result = facade.register("Jackaboy", "wapoosh", "ksdjhf");

        assertEquals("Jackaboy", result.get("username"));
        assertNotNull(result.get("authToken"));
    }

    @Test
    public void testRegisterFail() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> facade.register(null, "wapoosh", "ksdjhf"));
    }

    @Test
    public void testLoginPass() throws DataAccessException {
        HashMap<String, Object> result = facade.login("dippleDop", "impy");

        assertEquals("dippleDop", result.get("username"));
        assertNotNull(result.get("authToken"));
    }

    @Test
    public void testLoginFail() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> facade.login("illFail", "watch me fail"));
    }

    @Test
    public void testLogoutSuccess() throws DataAccessException {
        facade.login("dippleDop", "impy");

        HashMap<String, Object> result = facade.logout();
        assertNotNull(result);
    }

    @Test
    public void testLogoutFail() throws DataAccessException {
        facade.delete();
        assertThrows(DataAccessException.class, () -> facade.logout());
    }

    @Test
    public void testCreateGamePass() throws DataAccessException {
        HashMap<String, Object> result = facade.createGame("request");

        assertNotNull(result.get("gameID"));
    }

    @Test
    public void testCreateGameFail() throws DataAccessException{
        assertThrows(DataAccessException.class, () -> facade.createGame(null));
        facade.delete();
        assertThrows(DataAccessException.class, () -> facade.createGame("creative name"));
    }

    @Test
    public void testListGamePass() throws DataAccessException{
        facade.createGame("creative name");

        assertNotNull(facade.listGame());

        facade.createGame("another creative name");
        facade.createGame("im so creative");

        assertNotNull(facade.listGame());
    }

    @Test
    public void testListGameFail() throws DataAccessException{
        facade.delete();

        assertThrows(DataAccessException.class, () -> facade.listGame());
    }

    @Test
    public void testJoinGamePass() throws DataAccessException {
        HashMap<String, Object> gameResult = facade.createGame("creative name");
        assertDoesNotThrow(() -> facade.joinGame("WHITE", (Integer) gameResult.get("gameID")));
    }

    @Test
    public void testJoinGameFail() throws DataAccessException {
        HashMap<String, Object> gameResult = facade.createGame("creative name");
        facade.joinGame("WHITE", (Integer) gameResult.get("gameID"));
        assertThrows(DataAccessException.class, () -> facade.joinGame("WHITE", (Integer) gameResult.get("gameID")));
    }

    @Test
    public void testDeletePass() throws DataAccessException {
        facade.createGame("creative name");

        facade.createGame("another creative name");
        facade.createGame("im so creative");

        facade.delete();

        assertThrows(DataAccessException.class, () -> facade.login("dippleDop", "impy"));
    }
}
