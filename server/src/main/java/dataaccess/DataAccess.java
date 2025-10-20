package dataaccess;

import model.AuthData;
import model.UserData;

public interface DataAccess {
    void clear();
    void saveUser(UserData user);
    UserData getUser(String username);
    AuthData getAuth(String token);
    void deleteAuth(AuthData auth);
    void saveAuth(AuthData auth);
    void setCurrAuth(AuthData auth);
    AuthData getCurrAuth();
}
