package dataaccess;

import exception.ResponseException;
import model.AuthData;

import java.util.HashMap;

public interface AuthDataAccess {
    AuthData getAuth(String token) throws ResponseException;
    void deleteAuth(AuthData auth) throws ResponseException;
    void saveAuth(AuthData auth) throws ResponseException;
    void clear() throws ResponseException;
}
