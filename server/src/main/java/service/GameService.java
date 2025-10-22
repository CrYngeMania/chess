package service;

import chess.ChessGame;
import dataModel.*;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.GameDataAccess;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class GameService {
    private final GameDataAccess gameDataAccess;
    private final DataAccess dataAccess;

    public GameService(GameDataAccess gameDataAccess, DataAccess dataAccess) {
        this.gameDataAccess = gameDataAccess;
        this.dataAccess = dataAccess;
    }

    public Integer generateID() {
        while(true) {
            Random random = new Random();
            int bound = 1000000;
            int randomInt = random.nextInt(bound);
            if (gameDataAccess.getGame(randomInt) == null) {
                return randomInt;
            }
        }
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

    public CreateGameResult createGame(CreateGameRequest request, String authToken) throws DataAccessException {
        checkAuth(authToken);
        if (request.gameName() == null) {
            throw new DataAccessException(DataAccessException.Code.ClientError, "Error: No game name provided");
        }

        Integer gameID = generateID();
        gameDataAccess.saveGame(new GameData(gameID, null, null, request.gameName(), new ChessGame()));
        return new CreateGameResult(gameID);
    }

    public ListGameResult listGame(String authToken) throws DataAccessException{
        checkAuth(authToken);

        ArrayList<GameData> gamesList = gameDataAccess.getGamesList();
        return new ListGameResult(gamesList);

    }

    public JoinGameResult joinGame(JoinGameRequest request, String authToken) throws DataAccessException{
        checkAuth(authToken);
        AuthData currAuth = dataAccess.getAuth(authToken);

        GameData game = gameDataAccess.getGame(request.gameID());
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
            gameDataAccess.updateGame(request.gameID(), newGame);
        }
        else if (Objects.equals(request.playerColor(), "BLACK")){
            String checkColor = game.blackUsername();
            if (checkColor != null){
                throw new DataAccessException(DataAccessException.Code.TakenError, "Error: Already taken");
            }
            GameData newGame = new GameData(request.gameID(), game.whiteUsername(), currAuth.username(), game.gameName(), game.game());
            gameDataAccess.updateGame(request.gameID(), newGame);
        }
        return new JoinGameResult();
    }


}
