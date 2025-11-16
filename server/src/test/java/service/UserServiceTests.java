package service;
import dataaccess.MemoryAuthDataAccess;
import dataaccess.MemoryDataAccess;
import dataaccess.MemoryGameDataAccess;
import datamodel.*;
import exception.ResponseException;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests{
    static final UserService USER_SERVICE = new UserService(new MemoryDataAccess(), new MemoryGameDataAccess(), new MemoryAuthDataAccess());
    RegistrationRequest user = new RegistrationRequest("goodfarmswithscar", "well hello there", "struggling rn");
    RegistrationResult registrationResult;

    @BeforeEach

    void clear() throws ResponseException {
        USER_SERVICE.delete(null);
    }

    public void initRegister() throws ResponseException {
        registrationResult = USER_SERVICE.register(user);
    }

    @Test
    public void registerPass() throws ResponseException {
        RegistrationRequest[] testers = {
                new RegistrationRequest("tester", "well hello there", "struggling rn"),
                new RegistrationRequest("Cry", "well hello there", "ohboy")
        };
        for (RegistrationRequest user : testers) {
            RegistrationResult result = USER_SERVICE.register(user);
            assertEquals(user.username(), result.username());
            assertNotNull(result.authToken());

        }
    }



    @Test
    public void registerFailDupeNames() throws ResponseException {
        initRegister();
        RegistrationRequest tester1 = new RegistrationRequest("goodfarmswithscar", "well hello there", "struggling rn");

        assertThrows(ResponseException.class, () -> USER_SERVICE.register(tester1));
    }

    @Test
    public void registerFailNoName() throws ResponseException {

        RegistrationRequest tester1 = new RegistrationRequest(null, "well hello there", "struggling rn");

        assertThrows(ResponseException.class, () -> USER_SERVICE.register(tester1));
    }

    @Test
    public void loginPass() throws ResponseException {
        initRegister();
        LoginRequest request = new LoginRequest(user.username(), user.password());
        LoginResult result = USER_SERVICE.login(request);

        assertEquals(request.username(), result.username());
        assertNotNull(result.authToken());
    }

    @Test
    public void loginFail() throws ResponseException {
        initRegister();
        LoginRequest requestNoUser = new LoginRequest(null, user.password());


        assertThrows(ResponseException.class, () -> USER_SERVICE.login(requestNoUser));
        LoginRequest requestNoPass = new LoginRequest(user.username(), null);
        assertThrows(ResponseException.class, () -> USER_SERVICE.login(requestNoPass));
    }

    @Test
    public void logoutPass() throws ResponseException {
        initRegister();
        assertNotNull(USER_SERVICE.logout(registrationResult.authToken()));
    }

    @Test
    public void logoutFail() throws ResponseException {
        initRegister();
        assertThrows(ResponseException.class, () -> USER_SERVICE.logout(null));

        assertDoesNotThrow(() -> USER_SERVICE.logout(registrationResult.authToken()));
        assertThrows(ResponseException.class, () -> USER_SERVICE.logout(registrationResult.authToken()));
    }

    @Test
    public void clearPass() throws ResponseException {
        DeleteResult result = USER_SERVICE.delete(null);
    }
}
