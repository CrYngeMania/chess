package service;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.MySqlUserDataAccess;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SQLUserServiceTests {

    @Test
    public void saveUserSuccess() throws DataAccessException {
        MySqlUserDataAccess access = new MySqlUserDataAccess();


    }

    @Test
    public void saveUserFail() throws DataAccessException {

    }

    @Test
    public void getUserSuccess() throws DataAccessException {

    }

    @Test
    public void getUserFail() throws DataAccessException{

    }


}
