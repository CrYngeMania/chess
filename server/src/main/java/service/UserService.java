package service;


import dataModel.RegistrationResult;
import model.AuthData;
import model.UserData;
import dataaccess.DataAccess;
import java.util.UUID;



public class UserService {
    private DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public AuthData register(UserData user) throws Exception{
        if (dataAccess.getUser(user.username()) != null){
            throw new Exception("username taken error") ;
        }
        return new AuthData(user.username(), generateToken());

    }
}
