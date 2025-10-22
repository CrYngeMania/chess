package service;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDataAccess;
import dataaccess.MemoryDataAccess;
import dataaccess.MemoryGameDataAccess;
import datamodel.*;
import org.junit.jupiter.api.*;
import passoff.model.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests{
    static final UserService userService = new UserService(new MemoryDataAccess(), new MemoryGameDataAccess(), new MemoryAuthDataAccess());
    RegistrationRequest user = new RegistrationRequest("goodfarmswithscar", "well hello there", "struggling rn");
    RegistrationResult registrationResult;

    @BeforeEach

    void clear() throws DataAccessException {
        userService.delete(null);
    }

    public void initRegister() throws DataAccessException {
        registrationResult = userService.register(user);
    }

    @Test
    public void registerPass() throws DataAccessException {
        RegistrationRequest[] testers = {
                new RegistrationRequest("tester", "well hello there", "struggling rn"),
                new RegistrationRequest("Cry", "well hello there", "ohboy")
        };
        for (RegistrationRequest user : testers) {
            RegistrationResult result = userService.register(user);
            assertEquals(user.username(), result.username());
            assertNotNull(result.authToken());

        }
    }



    @Test
    public void registerFailDupeNames() throws DataAccessException {
        initRegister();
        RegistrationRequest tester1 = new RegistrationRequest("goodfarmswithscar", "well hello there", "struggling rn");

        assertThrows(DataAccessException.class, () -> userService.register(tester1));
    }

    @Test
    public void registerFailNoName() throws DataAccessException {

        RegistrationRequest tester1 = new RegistrationRequest(null, "well hello there", "struggling rn");

        assertThrows(DataAccessException.class, () -> userService.register(tester1));
    }

    @Test
    public void loginPass() throws DataAccessException {
        initRegister();
        LoginRequest request = new LoginRequest(user.username(), user.password());
        LoginResult result = userService.login(request);

        assertEquals(request.username(), result.username());
        assertNotNull(result.authToken());
    }

    @Test
    public void loginFail() throws DataAccessException {
        initRegister();
        LoginRequest requestNoUser = new LoginRequest(null, user.password());


        assertThrows(DataAccessException.class, () -> userService.login(requestNoUser));
        LoginRequest requestNoPass = new LoginRequest(user.username(), null);
        assertThrows(DataAccessException.class, () -> userService.login(requestNoPass));
    }

    @Test
    public void logoutPass() throws DataAccessException {
        initRegister();
        assertNotNull(userService.logout(registrationResult.authToken()));
    }

    @Test
    public void logoutFail() throws DataAccessException {
        initRegister();
        assertThrows(DataAccessException.class, () -> userService.logout(null));

        assertDoesNotThrow(() -> userService.logout(registrationResult.authToken()));
        assertThrows(DataAccessException.class, () -> userService.logout(registrationResult.authToken()));
    }

    @Test
    public void clearPass() throws DataAccessException {
        DeleteResult result = userService.delete(null);
    }
}
