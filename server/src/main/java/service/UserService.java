package service;


import chess.ChessGame;
import dataModel.*;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import dataaccess.DataAccess;

import java.util.ArrayList;
import java.util.Random;

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

    public void checkAuth(AuthData auth) throws DataAccessException{
        if (auth == null){
            throw new DataAccessException(DataAccessException.Code.UnauthorisedError, "Error: Unauthorised");
        }
        if (dataAccess.getAuth(auth.authToken()) == null) {
            throw new DataAccessException(DataAccessException.Code.UnauthorisedError, "Error: Unauthorised");
        }
    }

    public LogoutResult logout() throws DataAccessException {
        AuthData currAuth = dataAccess.getCurrAuth();
        checkAuth(currAuth);
        dataAccess.deleteAuth(currAuth);

        return new LogoutResult();
    }

    public Integer generateID() {
        while(true) {
            Random random = new Random();
            int bound = 1000000;
            int randomInt = random.nextInt(bound);
            if (dataAccess.getGame(randomInt) == null) {
                return randomInt;
            }
        }
    }

    public CreateGameResult createGame(CreateGameRequest request) throws DataAccessException {
        AuthData currAuth = dataAccess.getCurrAuth();
        checkAuth(currAuth);
        if (request.gameName() == null) {
            throw new DataAccessException(DataAccessException.Code.ClientError, "Error: No game name provided");
        }

        Integer gameID = generateID();
        dataAccess.saveGame(new GameData(gameID, null, null, request.gameName(), new ChessGame()));
        return new CreateGameResult(gameID);
    }

    public ListGameResult listGame() throws DataAccessException{
        AuthData currAuth = dataAccess.getCurrAuth();
        checkAuth(currAuth);

        ArrayList<GameData> gamesList = dataAccess.getGamesList();
        if (gamesList == null){
            return new ListGameResult(new ArrayList<>());
        }
        return new ListGameResult(gamesList);

    }

}
