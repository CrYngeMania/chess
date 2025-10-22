package service;

import dataModel.*;
import dataaccess.DataAccessException;
import dataaccess.GameDataAccess;
import model.AuthData;
import model.UserData;
import dataaccess.DataAccess;
import java.util.UUID;


public class UserService {
    private final DataAccess dataAccess;
    private final GameDataAccess gameDataAccess;

    public UserService(DataAccess dataAccess, GameDataAccess gameDataAccess) {
        this.dataAccess = dataAccess;
        this.gameDataAccess = gameDataAccess;
    }

    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    public RegistrationResult register(RegistrationRequest request) throws DataAccessException{
        if (dataAccess.getUser(request.username()) != null){
            throw new DataAccessException(DataAccessException.Code.TakenError, "Error: username already taken") ;
            /** username taken **/
        }
        if (request.password() == null){
            throw new DataAccessException(DataAccessException.Code.ClientError, "Error: No password provided");
        }
        dataAccess.saveUser(new UserData(request.username(), request.password(), request.email()));

        String token = generateToken();
        AuthData reg = new AuthData(request.username(), token);
        dataAccess.saveAuth(reg);

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
        String checkPassword = checkUser.password();
        if (!request.password().equals(checkPassword)){
            throw new DataAccessException(DataAccessException.Code.UnauthorisedError, "Error: Username/password is invalid");
        }

        String token = generateToken();
        AuthData reg = new AuthData(request.username(), token);
        dataAccess.saveAuth(reg);

        return new LoginResult(request.username(), token);
    }

    public void checkAuth(String authToken) throws DataAccessException{
        System.out.println(dataAccess.getAuths());
        if (authToken == null){
            throw new DataAccessException(DataAccessException.Code.UnauthorisedError, "Error: Unauthorised");
        }
        if (dataAccess.getAuth(authToken) == null) {
            throw new DataAccessException(DataAccessException.Code.UnauthorisedError, "Error: Unauthorised");
        }
    }

    public LogoutResult logout(String authToken) throws DataAccessException {
        checkAuth(authToken);
        AuthData currAuth = dataAccess.getAuth(authToken);
        dataAccess.deleteAuth(currAuth);

        return new LogoutResult();
    }

    public DeleteResult delete(String authToken) throws DataAccessException{
        dataAccess.clear();
        gameDataAccess.clear();
        return new DeleteResult();

    }
}
