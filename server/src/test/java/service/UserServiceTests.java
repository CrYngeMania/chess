package service;


import org.junit.jupiter.api.*;
import passoff.model.*;
import passoff.server.TestServerFacade;
import server.Server;

import java.net.HttpURLConnection;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class UserServiceTests {

    private static TestUser existingUser;
    private static TestUser newUser;
    private static TestServerFacade serverFacade;
    private static Server server;
    private String existingAuth;
    private static TestCreateRequest createRequest;


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
        newUser = new TestUser("NewUser", "newUserPassword", "nu@mail.com");
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
    public void register_success() {
        TestUser[] testers = {
                newUser,
                new TestUser("tester", "well hello there", "struggling rn"),
                new TestUser("goodfarmswithscar", "well hello there", "good"),
                new TestUser("Cry", "well hello there", "ohboy")
        };
        for (TestUser user : testers) {
            TestAuthResult registerResult = serverFacade.register(user);
            assertEquals(HttpURLConnection.HTTP_OK, serverFacade.getStatusCode());
        }
    }

    @Test
    public void register_fail_dupe_names(){

        TestUser tester1 = new TestUser("goodfarmswithscar", "well hello there", "struggling rn");
        TestUser tester1_copy = new TestUser("goodfarmswithscar", "well hello there", "good");


        TestAuthResult registerResult = serverFacade.register(tester1);
        TestAuthResult registerResult2 = serverFacade.register(tester1_copy);
        assertHttpForbidden(registerResult2);
    }

    @Test
    public void register_fail_no_name(){

        TestUser tester1 = new TestUser(null, "well hello there", "struggling rn");

        TestAuthResult registerResult = serverFacade.register(tester1);
        assertHttpBadRequest(registerResult);
    }

    @Test
    public void login_success(){
        TestAuthResult loginResult = serverFacade.login(existingUser);
        assertEquals(HttpURLConnection.HTTP_OK, serverFacade.getStatusCode());
    }

    @Test
    public void login_fail(){
        TestAuthResult loginResult = serverFacade.login(new TestUser(existingUser.getUsername(), null, existingUser.getEmail()));
        assertHttpBadRequest(loginResult);

        TestAuthResult loginResultUser = serverFacade.login(new TestUser(null, existingUser.getPassword(), existingUser.getEmail()));
        assertHttpBadRequest(loginResult);

        TestAuthResult loginResultBadPassword = serverFacade.login(new TestUser(existingUser.getUsername(), "Well hello there", existingUser.getEmail()));
        assertHttpUnauthorized(loginResult);
    }

    @Test
    public void logout_success(){
        TestResult logoutResult = serverFacade.logout(existingAuth);
        assertEquals(HttpURLConnection.HTTP_OK, serverFacade.getStatusCode());
    }

    @Test
    public void logout_fail(){
        TestResult logoutResult = serverFacade.logout(null);
        assertHttpUnauthorized(logoutResult);
        TestResult logoutResultFirst = serverFacade.logout(existingAuth);
        TestResult logoutResultSecond = serverFacade.logout(existingAuth);
        assertHttpUnauthorized(logoutResultSecond);

    }

    @Test
    public void clear_pass(){
        TestResult clearResult = serverFacade.clear();
        assertEquals(HttpURLConnection.HTTP_OK, serverFacade.getStatusCode());

        TestCreateResult createGameResult = serverFacade.createGame(createRequest, existingAuth);
        TestCreateResult createGameResult2 = serverFacade.createGame(new TestCreateRequest("Well hello there"), existingAuth);
        TestResult clearMultiple = serverFacade.clear();
        assertEquals(HttpURLConnection.HTTP_OK, serverFacade.getStatusCode());
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
