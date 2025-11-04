package service;

import datamodel.*;
import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.GameDataAccess;
import model.AuthData;
import model.UserData;
import dataaccess.DataAccess;
import java.util.UUID;


public class UserService {
    private final DataAccess dataAccess;
    private final GameDataAccess gameDataAccess;
    private final AuthDataAccess authDataAccess;
    private final AuthService authService;

    public UserService(DataAccess dataAccess, GameDataAccess gameDataAccess, AuthDataAccess authDataAccess) {
        this.dataAccess = dataAccess;
        this.gameDataAccess = gameDataAccess;
        this.authDataAccess = authDataAccess;
        this.authService = new AuthService(authDataAccess);

    }

    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    public RegistrationResult register(RegistrationRequest request) throws DataAccessException{
        if (dataAccess.getUser(request.username()) != null){
            throw new DataAccessException(DataAccessException.Code.TakenError, "Error: username already taken") ;
            /** username taken **/
        }
        if (request.username() == null){
            throw new DataAccessException(DataAccessException.Code.ClientError, "Error: No username provided");
        }
        if (request.password() == null){
            throw new DataAccessException(DataAccessException.Code.ClientError, "Error: No password provided");
        }
        dataAccess.saveUser(new UserData(request.username(), request.password(), request.email()));

        String token = generateToken();
        AuthData reg = new AuthData(request.username(), token);
        authDataAccess.saveAuth(reg);

        return new RegistrationResult(request.username(), token);

    }

    public LoginResult login(LoginRequest request) throws DataAccessException{
        UserData checkUser = dataAccess.getUser(request.username());
        if (request.username() == null){
            throw new DataAccessException(DataAccessException.Code.ClientError, "Error: No username provided");
        }
        if ( checkUser == null){
            throw new DataAccessException(DataAccessException.Code.UnauthorisedError, "Error: Username/password is invalid");
        }
        if (request.password() == null){
            throw new DataAccessException(DataAccessException.Code.ClientError, "Error: No password provided");
        }
        if (dataAccess.verifyUser(checkUser.username(), checkUser.password())){
            throw new DataAccessException(DataAccessException.Code.UnauthorisedError, "Error: Username/password is invalid");
        }

        String token = generateToken();
        AuthData reg = new AuthData(request.username(), token);
        authDataAccess.saveAuth(reg);

        return new LoginResult(request.username(), token);
    }

    public LogoutResult logout(String authToken) throws DataAccessException {
        authService.checkAuth(authToken);
        AuthData currAuth = authDataAccess.getAuth(authToken);
        authDataAccess.deleteAuth(currAuth);

        return new LogoutResult();
    }

    public DeleteResult delete(String authToken) throws DataAccessException{
        dataAccess.clear();
        gameDataAccess.clear();
        authDataAccess.clear();
        return new DeleteResult();

    }
}
