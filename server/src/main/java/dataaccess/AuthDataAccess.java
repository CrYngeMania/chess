package dataaccess;

import model.AuthData;

import java.util.HashMap;

public interface AuthDataAccess {
    AuthData getAuth(String token) throws DataAccessException;
    void deleteAuth(AuthData auth) throws DataAccessException;
    void saveAuth(AuthData auth) throws DataAccessException;
    void clear() throws DataAccessException;
}
