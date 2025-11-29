package service;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import exception.ResponseException;

public class AuthService {
    private final AuthDataAccess authDataAccess;

    public AuthService(AuthDataAccess authDataAccess) {
        this.authDataAccess = authDataAccess;
    }

    public void checkAuth(String authToken) throws ResponseException {
        if (authToken == null){
            throw new ResponseException(ResponseException.Code.UnauthorisedError, " Unauthorised");
        }
        if (authDataAccess.getAuth(authToken) == null) {
            throw new ResponseException(ResponseException.Code.UnauthorisedError, " Unauthorised");
        }
    }
}
