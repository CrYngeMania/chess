package service;


import dataModel.LoginResult;
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



    public RegistrationResult register(UserData user) throws DataAccessException{
        if (dataAccess.getUser(user.username()) != null){
            throw new DataAccessException(DataAccessException.Code.TakenError, "Error: username already taken") ;
            /** username taken **/
        }
        if (user.password() == null){
            throw new DataAccessException(DataAccessException.Code.ClientError, "Error: No password provided");
        }
        dataAccess.saveUser(user);
        return new RegistrationResult(user.username(), generateToken());

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
        return new LoginResult(user.username(), generateToken());
    }
}
