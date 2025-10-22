package service;


import org.junit.jupiter.api.*;
import passoff.model.*;
import passoff.server.TestServerFacade;
import server.Server;

import java.net.HttpURLConnection;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class GameServiceTests {

    private static TestUser existingUser;
    private static TestCreateRequest createRequest;
    private static TestServerFacade serverFacade;
    private static Server server;
    private String existingAuth;


    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        serverFacade = new TestServerFacade("localhost", Integer.toString(port));
        existingUser = new TestUser("ExistingUser", "existingUserPassword", "eu@mail.com");
        createRequest = new TestCreateRequest("testGame");
    }

    @BeforeEach
    public void setup() {
        serverFacade.clear();

        //one user already logged in
        TestAuthResult regResult = serverFacade.register(existingUser);
        existingAuth = regResult.getAuthToken();
    }



    @Test
    public void createGamePass() {
        TestCreateResult createGameResult = serverFacade.createGame(createRequest, existingAuth);
        assertEquals(HttpURLConnection.HTTP_OK, serverFacade.getStatusCode());

    }

    @Test
    public void createFail() {
        TestCreateResult createGameResultNoAuth = serverFacade.createGame(createRequest, null);
        assertHttpUnauthorized(createGameResultNoAuth);
        TestCreateResult createGameResult = serverFacade.createGame(new TestCreateRequest(null), existingAuth);
        assertHttpBadRequest(createGameResult);
    }

    @Test
    public void listGamesPass(){
        TestListResult listGamesResult = serverFacade.listGames(existingAuth);
        assertEquals(HttpURLConnection.HTTP_OK, serverFacade.getStatusCode());

        TestCreateResult createGameResult1 = serverFacade.createGame(new TestCreateRequest("hello miners and crafters"), existingAuth);
        TestCreateResult createGameResult = serverFacade.createGame(new TestCreateRequest("Dipple Dop!"), existingAuth);
        TestCreateResult createGameResult2 = serverFacade.createGame(new TestCreateRequest("In Oli we trust"), existingAuth);

        TestListResult listGamesResult2 = serverFacade.listGames(existingAuth);
        assertEquals(HttpURLConnection.HTTP_OK, serverFacade.getStatusCode());

    }

    @Test
    public void listGamesFail(){
        TestListResult listGamesResult = serverFacade.listGames(null);
        assertHttpUnauthorized(listGamesResult);
    }

    @Test
    public void joinGamePass(){
        TestCreateResult createGame = serverFacade.createGame(new TestCreateRequest("hello miners and crafters"), existingAuth);
        TestResult joinGameResult = serverFacade.joinPlayer(new TestJoinRequest("WHITE", createGame.getGameID()), existingAuth);
        TestResult joinGameResult2 = serverFacade.joinPlayer(new TestJoinRequest("BLACK", createGame.getGameID()), existingAuth);
        assertEquals(HttpURLConnection.HTTP_OK, serverFacade.getStatusCode());
    }

    @Test
    public void joinGameFail(){
        TestCreateResult createGame = serverFacade.createGame(new TestCreateRequest("hello miners and crafters"), existingAuth);
        TestResult joinGameResult = serverFacade.joinPlayer(new TestJoinRequest("WHITE", createGame.getGameID()), existingAuth);
        TestResult joinGameResult2 = serverFacade.joinPlayer(new TestJoinRequest("WHITE", createGame.getGameID()), existingAuth);
        assertHttpForbidden(joinGameResult2);

        TestResult joinGameResultWrongColor = serverFacade.joinPlayer(new TestJoinRequest("ORANGE", createGame.getGameID()), existingAuth);
        assertHttpBadRequest(joinGameResultWrongColor);
    }




    private void assertHttpBadRequest(TestResult result) {
        assertHttpError(result, HttpURLConnection.HTTP_BAD_REQUEST, "Bad Request");
    }

    private void assertHttpUnauthorized(TestResult result) {
        assertHttpError(result, HttpURLConnection.HTTP_UNAUTHORIZED, "Unauthorized");
    }

    private void assertHttpForbidden(TestResult result) {
        assertHttpError(result, HttpURLConnection.HTTP_FORBIDDEN, "Forbidden");
    }

    private void assertHttpError(TestResult result, int statusCode, String message) {
        Assertions.assertEquals(statusCode, serverFacade.getStatusCode(),
                "Server response code was not %d %s (message: %s)".formatted(statusCode, message, result.getMessage()));
        Assertions.assertNotNull(result.getMessage(), "Invalid Request didn't return an error message");
        Assertions.assertTrue(result.getMessage().toLowerCase(Locale.ROOT).contains("error"),
                "Error message didn't contain the word \"Error\"");
    }



}
