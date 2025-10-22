package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDataAccess implements AuthDataAccess{
    private HashMap<String, AuthData> auths = new HashMap<>();

    @Override
    public void saveAuth(AuthData auth) {
        auths.put(auth.authToken(), auth);
    }

    @Override
    public void deleteAuth(AuthData auth) {
        auths.remove(auth.authToken());
    }

    @Override
    public AuthData getAuth(String token) {
        return auths.get(token);
    }

    public HashMap<String, AuthData> getAuths(){
        return auths;
    }

    public void clear(){
        auths.clear();
    }
}
