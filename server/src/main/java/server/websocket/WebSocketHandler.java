package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.AuthDataAccess;
import dataaccess.MySqlAuthDataAccess;
import dataaccess.MySqlGameDataAccess;
import exception.ResponseException;
import io.javalin.websocket.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.GameNotificationMessage;
import websocket.messages.LoadGameMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsCloseHandler, WsMessageHandler {
    AuthDataAccess authDAO = new MySqlAuthDataAccess();


    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) throws Exception {
        System.out.print("Websocket closed");
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) throws Exception {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) throws Exception {
        Session session = ctx.session;
        Gson gson = new Gson();
        try{
            UserGameCommand command = gson.fromJson(ctx.message(), UserGameCommand.class);

            switch(command.getCommandType()){
                case CONNECT -> connect(command.getAuthToken(), command.getGameID(), ctx.session);
                case MAKE_MOVE -> makeMove(command.getAuthToken(),command, ctx.session);
                case LEAVE -> leave(command.getAuthToken(), command.getGameID(), ctx.session);
                case RESIGN -> resign(command.getAuthToken(), command.getGameID(), ctx.session);
            }
        } catch (Exception e) {
            var err = new ErrorMessage("Error: " + e.getMessage());
            ctx.session.getRemote().sendString(gson.toJson(err));
        }
    }

    private void saveSession(int gameID, String username, Session session) {
        connections.add(username, gameID, session);

    }

    public void makeMove(String authToken, UserGameCommand command, Session session) throws IOException, ResponseException, InvalidMoveException {
        String username = getUsername(authToken);
        int gameID = command.getGameID();

        MakeMoveCommand moveCommand = (MakeMoveCommand) command;

        GameData game = new MySqlGameDataAccess().getGame(gameID);
        game.game().makeMove(moveCommand.getMove());

        LoadGameMessage update = new LoadGameMessage(game.game());
        connections.broadcast(gameID, null, update);

        var message = String.format("%s made a move!", username);
        var notification = new GameNotificationMessage(message);
        connections.broadcast(gameID, session, notification);
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

        var notification = new GameNotificationMessage(username + "entered the game!");
        connections.broadcast(gameID, session, notification);
    }
}
