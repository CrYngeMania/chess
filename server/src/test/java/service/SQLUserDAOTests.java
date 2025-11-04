package service;

import dataaccess.DataAccessException;
import dataaccess.MySqlUserDataAccess;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SQLUserDAOTests {

    private final MySqlUserDataAccess access = new MySqlUserDataAccess();

    @BeforeEach
    void setup() throws Exception {
        access.configureDatabase();
        access.clear();
    }

    @Test
    public void saveUserSuccess() throws DataAccessException {
        UserData user = new UserData("ScarGoodtimes", "dippledop", "mail");
        access.saveUser(user);

        var result = access.getUser("ScarGoodtimes");
        assertNotNull(result);
        assertEquals(user.username(), result.username());
        assertNotEquals(user.password(), result.password());
    }

    @Test
    public void saveUserSuccessMultiple() throws DataAccessException {
        UserData user = new UserData("ScarGoodtimes", "dippledop", "mail");
        access.saveUser(user);
        UserData user2 = new UserData("Its 2AM rn", "dippledop", "mail");
        access.saveUser(user2);
        UserData user3 = new UserData("im so tired", "dippledop", "mail");
        access.saveUser(user3);

        var result = access.getUser("ScarGoodtimes");
        var result2 = access.getUser(user2.username());
        var result3 = access.getUser(user3.username());
        assertNotNull(result);
        assertNotNull(result2);
        assertNotNull(result3);
        assertEquals(user.username(), result.username());
        assertNotEquals(user.password(), result.password());
        assertEquals(user2.username(), result2.username());
        assertNotEquals(user2.password(), result2.password());
        assertEquals(user3.username(), result3.username());
        assertNotEquals(user3.password(), result3.password());
    }

    @Test
    public void saveUserFailNoUsername() {
        UserData user = new UserData(null, "dippledop", "mail");
        assertThrows(DataAccessException.class, () -> access.saveUser(user));
    }

    @Test
    public void saveUserFailDuplicate() throws DataAccessException {
        UserData user = new UserData("ScarGoodtimes", "dippledop", "mail");
        access.saveUser(user);
        UserData user2 = new UserData("ScarGoodtimes", "dippledop", "mail");
        assertThrows(DataAccessException.class, () -> access.saveUser(user2));
    }

    @Test
    public void getUserSuccess() throws DataAccessException {
        UserData user = new UserData("ScarGoodtimes", "dippledop", "mail");
        access.saveUser(user);

        UserData user2 = new UserData("Mumbo Jumbolio", "dippledop", "mail");
        access.saveUser(user2);

        var result1 = access.getUser("ScarGoodtimes");
        var result2 = access.getUser("Mumbo Jumbolio");
        assertNotNull(result1);
        assertEquals(user.username(), result1.username());
        assertNotEquals(user.password(), result1.password());

        assertNotNull(result2);
        assertEquals(user2.username(), result2.username());
        assertNotEquals(user2.password(), result2.password());
    }

    @Test
    public void getUserFail() throws DataAccessException{
        UserData user = new UserData("ScarGoodtimes", "dippledop", "mail");
        access.saveUser(user);
        assertNull(access.getUser(null));
    }

    @Test
    public void verifyUserSuccess() throws DataAccessException {
        UserData user = new UserData("ScarGoodtimes", "dippledop", "mail");
        access.saveUser(user);

        assertTrue(access.verifyUser(user.username(), user.password()));
    }

    @Test
    public void verifyUserFail() throws DataAccessException {
        UserData user = new UserData("Scarrrrrr", "dippledop", "mail");
        access.saveUser(user);

        assertFalse(access.verifyUser(user.username(), "hi"));
    }

    @Test
    public void verifyUserNoUsername() throws DataAccessException {
        UserData user = new UserData("Scarrrrrr", "dippledop", "mail");
        access.saveUser(user);

        assertFalse(access.verifyUser(null, user.password()));
    }

    @Test
    public void clearSuccess() throws DataAccessException {
        UserData user = new UserData("whats up", "dippledop", "mail");
        access.saveUser(user);

        access.clear();
        assertNull(access.getUser(user.username()));
    }
}
