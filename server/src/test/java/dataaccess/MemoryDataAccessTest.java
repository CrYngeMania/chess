package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemoryDataAccessTest {

    @Test
    void clear() {
        var user = new UserData("joe", "j@j", "j");
        DataAccess da = new MemoryDataAccess();
        da.clear();
    }

    @Test
    void saveUser() {
    }

    @Test
    void getUser() {
    }
}