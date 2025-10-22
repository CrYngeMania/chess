package service;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;

public class AuthService {
    private final AuthDataAccess authDataAccess;

    public AuthService(AuthDataAccess authDataAccess) {
        this.authDataAccess = authDataAccess;
    }

    public void checkAuth(String authToken) throws DataAccessException {
        if (authToken == null){
            throw new DataAccessException(DataAccessException.Code.UnauthorisedError, "Error: Unauthorised");
        }
        if (authDataAccess.getAuth(authToken) == null) {
            throw new DataAccessException(DataAccessException.Code.UnauthorisedError, "Error: Unauthorised");
        }
    }
}
