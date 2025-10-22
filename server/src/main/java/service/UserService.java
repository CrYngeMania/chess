package service;


import chess.ChessGame;
import dataModel.*;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import dataaccess.DataAccess;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;


public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
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

        AuthData reg = new AuthData(request.username(), generateToken());
        dataAccess.saveAuth(reg);

        return new LoginResult(request.username(), generateToken());
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

    public CreateGameResult createGame(CreateGameRequest request, String authToken) throws DataAccessException {
        checkAuth(authToken);
        if (request.gameName() == null) {
            throw new DataAccessException(DataAccessException.Code.ClientError, "Error: No game name provided");
        }

        Integer gameID = generateID();
        dataAccess.saveGame(new GameData(gameID, null, null, request.gameName(), new ChessGame()));
        return new CreateGameResult(gameID);
    }

    public ListGameResult listGame(String authToken) throws DataAccessException{
        checkAuth(authToken);

        ArrayList<GameData> gamesList = dataAccess.getGamesList();
        return new ListGameResult(gamesList);

    }

    public JoinGameResult joinGame(JoinGameRequest request, String authToken) throws DataAccessException{
        checkAuth(authToken);
        AuthData currAuth = dataAccess.getAuth(authToken);

        GameData game = dataAccess.getGame(request.gameID());
        if (game == null) {
            throw new DataAccessException(DataAccessException.Code.ClientError, "Error: No game exists");
        }
        if (!Objects.equals(request.playerColor(), "WHITE") && !Objects.equals(request.playerColor(), "BLACK")){
            throw new DataAccessException(DataAccessException.Code.ClientError, "Error: Invalid Color");
        }
        if (Objects.equals(request.playerColor(), "WHITE")){
            String checkColor = game.whiteUsername();
            if (checkColor != null){
                throw new DataAccessException(DataAccessException.Code.TakenError, "Error: Already taken");
            }
            GameData newGame = new GameData(request.gameID(), currAuth.username(), game.blackUsername(), game.gameName(), game.game());
            dataAccess.updateGame(request.gameID(), newGame);
        }
        else if (Objects.equals(request.playerColor(), "BLACK")){
            String checkColor = game.blackUsername();
            if (checkColor != null){
                throw new DataAccessException(DataAccessException.Code.TakenError, "Error: Already taken");
            }
            GameData newGame = new GameData(request.gameID(), game.whiteUsername(), currAuth.username(), game.gameName(), game.game());
            dataAccess.updateGame(request.gameID(), newGame);
        }
        return new JoinGameResult();
    }

    public DeleteResult delete(String authToken) throws DataAccessException{
        dataAccess.clear();
        return new DeleteResult();

    }
}
