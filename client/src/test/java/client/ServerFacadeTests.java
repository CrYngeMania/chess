package client;

import exception.ResponseException;
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
    public void testRegisterPass() throws ResponseException {
        HashMap<String, Object> result = facade.register("Jackaboy", "wapoosh", "ksdjhf");

        assertEquals("Jackaboy", result.get("username"));
        assertNotNull(result.get("authToken"));
    }

    @Test
    public void testRegisterFail() throws ResponseException {
        assertThrows(ResponseException.class, () -> facade.register(null, "wapoosh", "ksdjhf"));
    }

    @Test
    public void testLoginPass() throws ResponseException {
        HashMap<String, Object> result = facade.login("dippleDop", "impy");

        assertEquals("dippleDop", result.get("username"));
        assertNotNull(result.get("authToken"));
    }

    @Test
    public void testLoginFail() throws ResponseException {
        assertThrows(ResponseException.class, () -> facade.login("illFail", "watch me fail"));
    }

    @Test
    public void testLogoutSuccess() throws ResponseException {
        facade.login("dippleDop", "impy");

        HashMap<String, Object> result = facade.logout();
        assertNotNull(result);
    }

    @Test
    public void testLogoutFail() throws ResponseException {
        facade.delete();
        assertThrows(ResponseException.class, () -> facade.logout());
    }

    @Test
    public void testCreateGamePass() throws ResponseException {
        HashMap<String, Object> result = facade.createGame("request");

        assertNotNull(result.get("gameID"));
    }

    @Test
    public void testCreateGameFail() throws ResponseException{
        assertThrows(ResponseException.class, () -> facade.createGame(null));
        facade.delete();
        assertThrows(ResponseException.class, () -> facade.createGame("creative name"));
    }

    @Test
    public void testListGamePass() throws ResponseException{
        facade.createGame("creative name");

        assertNotNull(facade.listGame());

        facade.createGame("another creative name");
        facade.createGame("im so creative");

        assertNotNull(facade.listGame());
    }

    @Test
    public void testListGameFail() throws ResponseException{
        facade.delete();

        assertThrows(ResponseException.class, () -> facade.listGame());
    }

    @Test
    public void testJoinGamePass() throws ResponseException {
        HashMap<String, Object> gameResult = facade.createGame("creative name");
        assertDoesNotThrow(() -> facade.joinGame("WHITE", (Integer) gameResult.get("gameID")));
    }

    @Test
    public void testJoinGameFail() throws ResponseException {
        HashMap<String, Object> gameResult = facade.createGame("creative name");
        facade.joinGame("WHITE", (Integer) gameResult.get("gameID"));
        assertThrows(ResponseException.class, () -> facade.joinGame("WHITE", (Integer) gameResult.get("gameID")));
    }

    @Test
    public void testDeletePass() throws ResponseException {
        facade.createGame("creative name");

        facade.createGame("another creative name");
        facade.createGame("im so creative");

        facade.delete();

        assertThrows(ResponseException.class, () -> facade.login("dippleDop", "impy"));
    }
}
