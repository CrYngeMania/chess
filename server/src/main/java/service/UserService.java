package service;


import dataModel.CreateGameResult;
import dataModel.LoginResult;
import dataModel.LogoutResult;
import dataModel.RegistrationResult;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import dataaccess.DataAccess;

import static server.Server.generateToken;


public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }



    public AuthData register(UserData user) throws DataAccessException{
        if (dataAccess.getUser(user.username()) != null){
            throw new DataAccessException(DataAccessException.Code.TakenError, "Error: username already taken") ;
            /** username taken **/
        }
        if (user.password() == null){
            throw new DataAccessException(DataAccessException.Code.ClientError, "Error: No password provided");
        }
        dataAccess.saveUser(user);

        AuthData reg = new AuthData(user.username(), generateToken());
        dataAccess.saveAuth(reg);

        dataAccess.setCurrAuth(reg);
        return reg;

    }

    public LoginResult login(UserData user) throws DataAccessException{
        UserData checkUser = dataAccess.getUser(user.username());
        if ( checkUser == null){
            throw new DataAccessException(DataAccessException.Code.UnauthorisedError, "Error: Username/password is invalid");
        }
        if (user.password() == null){
            throw new DataAccessException(DataAccessException.Code.ClientError, "Error: No password provided");
        }
        String checkPassword = checkUser.password();
        if (!user.password().equals(checkPassword)){
            throw new DataAccessException(DataAccessException.Code.UnauthorisedError, "Error: Username/password is invalid");
        }
        dataAccess.saveUser(user);

        AuthData reg = new AuthData(user.username(), generateToken());
        dataAccess.saveAuth(reg);
        dataAccess.setCurrAuth(reg);

        return new LoginResult(user.username(), generateToken());
    }

    public LogoutResult logout() throws DataAccessException{
        AuthData currAuth = dataAccess.getCurrAuth();
        if (currAuth == null){
            throw new DataAccessException(DataAccessException.Code.UnauthorisedError, "Error: Unauthorised");
        }
        if (dataAccess.getAuth(currAuth.authToken()) == null) {
            throw new DataAccessException(DataAccessException.Code.UnauthorisedError, "Error: Unauthorised");
        }
        dataAccess.deleteAuth(currAuth);

        return new LogoutResult();
    }

    public CreateGameResult createGame(String gameName){

    }
}
