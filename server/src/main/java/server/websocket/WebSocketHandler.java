package server.websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDataAccess;
import dataaccess.GameDataAccess;
import dataaccess.MySqlAuthDataAccess;
import dataaccess.MySqlGameDataAccess;
import exception.ResponseException;
import io.javalin.websocket.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.GameNotificationMessage;
import websocket.messages.LoadGameMessage;

import java.io.IOException;
import java.util.Objects;

public class WebSocketHandler implements WsConnectHandler, WsCloseHandler, WsMessageHandler {
    AuthDataAccess authDAO = new MySqlAuthDataAccess();
    GameDataAccess gameDAO = new MySqlGameDataAccess();


    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext){
        System.out.print("Websocket closed");
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) throws Exception {
        Gson gson = new Gson();
        try{
            System.out.println(ctx.message());
            UserGameCommand command = gson.fromJson(ctx.message(), UserGameCommand.class);

            switch(command.getCommandType()){
                case CONNECT -> connect(command.getAuthToken(), command.getGameID(), ctx.session);
                case MAKE_MOVE -> { MakeMoveCommand moveCommand = gson.fromJson(ctx.message(), MakeMoveCommand.class);
                    makeMove(moveCommand.getAuthToken(),moveCommand, ctx.session);
                }
                case LEAVE -> leave(command.getAuthToken(), command.getGameID(), ctx.session);
                case RESIGN -> resign(command.getAuthToken(), command.getGameID(), ctx.session);
            }
        } catch (Exception e) {
            var err = new ErrorMessage("Error: " + e.getMessage());
            ctx.session.getRemote().sendString(gson.toJson(err));
        }
    }

    public void makeMove(String authToken, MakeMoveCommand moveCommand, Session session) throws IOException, ResponseException, InvalidMoveException {
        String username = getUsername(authToken);
        int gameID = moveCommand.getGameID();
        ChessGame.TeamColor color;

        GameData game = new MySqlGameDataAccess().getGame(gameID);
        ChessGame newGame = game.game();
        newGame.makeMove(moveCommand.getMove());
        GameData newGameData = new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), newGame);
        gameDAO.updateGame(game.gameID(), newGameData);


        if (Objects.equals(username, game.blackUsername())){
            color = ChessGame.TeamColor.WHITE;
        }
        else{color = ChessGame.TeamColor.BLACK;}

        LoadGameMessage update = new LoadGameMessage(game.game());
        connections.broadcast(gameID, null, update);

        if (game.game().isInCheckmate(color)){
            var message = String.format("%s wins! What a champ!", username);
            var notification = new GameNotificationMessage(message);
            connections.broadcast(gameID, null, notification);
        }
        else if(game.game().isInStalemate(color)){
            var message = "Oh dear..... looks like a stalemate :(";
            var notification = new GameNotificationMessage(message);
            connections.broadcast(gameID, null, notification);
        }
        else{
            var message = String.format("%s made a move!", username);
            var notification = new GameNotificationMessage(message);
            connections.broadcast(gameID, session, notification);
        }
    }

    public void resign(String authToken, Integer gameID, Session session) throws IOException {
        String username = getUsername(authToken);

        var message = String.format("%s resigned!", username);
        var notification = new GameNotificationMessage(message);
        connections.broadcast(gameID, session, notification);
    }

    public void leave(String authToken, Integer gameID, Session session) throws IOException {
        String username = getUsername(authToken);

        var message = String.format("%s left the game!", username);
        var notification = new GameNotificationMessage(message);
        connections.broadcast(gameID, session, notification);
        connections.remove(gameID, username);
    }

    private String getUsername(String authToken) {
        try {
            return authDAO.getAuth(authToken).username();
        } catch (Exception e) {
            throw new RuntimeException("Invalid auth token");
        }
    }

    public void connect(String authToken, Integer gameID, Session session) throws IOException, ResponseException {
        String username = getUsername(authToken);

        connections.add(username, gameID, session);
        System.out.println("Websocket connected");

        GameData game = new MySqlGameDataAccess().getGame(gameID);
        ChessGame chess = game.game();
        LoadGameMessage load = new LoadGameMessage(chess);
        session.getRemote().sendString(new Gson().toJson(load));

        var notification = new GameNotificationMessage(username + " entered the game!");
        connections.broadcast(gameID, session, notification);
    }
}
