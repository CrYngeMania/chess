package dataaccess;

import model.AuthData;

import java.util.HashMap;

public interface AuthDataAccess {
    AuthData getAuth(String token);
    void deleteAuth(AuthData auth);
    void saveAuth(AuthData auth);
    void clear();
}
