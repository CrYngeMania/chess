package service;

import dataaccess.DataAccessException;
import dataaccess.MySqlAuthDataAccess;
import dataaccess.MySqlUserDataAccess;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class SQLAuthServiceTests {

    private MySqlUserDataAccess userAccess = new MySqlUserDataAccess();
    private MySqlAuthDataAccess authAccess = new MySqlAuthDataAccess();

    @BeforeEach
    void setup() throws Exception {
        userAccess.configureDatabase();
        userAccess.clear();
    }

    @Test
    void saveAuthSuccess() throws DataAccessException {
        AuthData auth = new AuthData("ScarGoodtimes", UUID.randomUUID().toString());
        authAccess.saveAuth(auth);

        var result = authAccess.getAuth(auth.authToken());
        assertNotNull(result);
        assertEquals(result.username(), auth.username());
    }

    @Test
    void saveAuthFail() throws DataAccessException {
        AuthData auth = new AuthData(null, null);
        assertThrows(DataAccessException.class, () -> authAccess.saveAuth(auth));
    }

    @Test
    void getAuthSuccess() throws DataAccessException {
        AuthData auth = new AuthData("ScarGoodtimes", UUID.randomUUID().toString());
        authAccess.saveAuth(auth);

        AuthData auth2 = new AuthData("Mumbo Jumbolio", UUID.randomUUID().toString());
        authAccess.saveAuth(auth2);

        var result1 = authAccess.getAuth(auth.authToken());
        var result2 = authAccess.getAuth(auth2.authToken());
        assertNotNull(result1);
        assertEquals(auth.username(), result1.username());
        assertEquals(auth.authToken(), result1.authToken());

        assertNotNull(result2);
        assertEquals(auth2.username(), result2.username());
        assertEquals(auth2.authToken(), result2.authToken());
    }

    @Test
    void getAuthFail() throws DataAccessException {
        assertNull(authAccess.getAuth("whoops"));
    }

    @Test
    void deleteAuthSuccess() throws DataAccessException {
        AuthData auth = new AuthData("ScarGoodtimes", UUID.randomUUID().toString());
        authAccess.saveAuth(auth);

        var result = authAccess.getAuth(auth.authToken());
        assertNotNull(result);
        assertEquals(result.username(), auth.username());

        authAccess.deleteAuth(auth);

        assertNull(authAccess.getAuth(auth.authToken()));
    }

    @Test
    public void clearSuccess() throws DataAccessException {
        AuthData auth = new AuthData("ScarGoodtimes", UUID.randomUUID().toString());
        authAccess.saveAuth(auth);

        authAccess.clear();
        assertNull(authAccess.getAuth(auth.authToken()));
    }
}
