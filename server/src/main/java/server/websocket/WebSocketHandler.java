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
import websocket.messages.GameNotificationMessage;
import websocket.messages.LoadGameMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsCloseHandler, WsMessageHandler {
    AuthDataAccess authDAO = new MySqlAuthDataAccess();
    private int gameID;

    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) throws Exception {
        System.out.print("Websocket closed");
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext wsConnectContext) throws Exception {

    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) throws Exception {
        gameID = -1;
        Session session = ctx.session;
        try{
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);

            gameID = command.getGameID();
            String username = getUsername(command.getAuthToken());
            saveSession(gameID, username, session);

            switch(command.getCommandType()){
                case CONNECT -> connect(username, session);
                case MAKE_MOVE -> {
                    MakeMoveCommand moveCommand = new Gson().fromJson(ctx.message(), MakeMoveCommand.class);
                    makeMove(username, moveCommand.getMove(), session);
                }
                case LEAVE -> leave(username, session);
                case RESIGN -> resign(username, session);
            }
        } catch (JsonSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveSession(int gameID, String username, Session session) {
        connections.add(username, gameID, session);

    }

    public void makeMove(String username, ChessMove move, Session session) throws IOException, ResponseException, InvalidMoveException {
        GameData game = new MySqlGameDataAccess().getGame(gameID);
        game.game().makeMove(move);

        LoadGameMessage update = new LoadGameMessage(game.game());
        connections.broadcast(gameID, null, update);

        var message = String.format("%s made a move!", username);
        var notification = new GameNotificationMessage(message);
        connections.broadcast(gameID, session, notification);
    }

    public void resign(String username, Session session) throws IOException {
        var message = String.format("%s resigned!", username);
        var notification = new GameNotificationMessage(message);
        connections.broadcast(gameID, session, notification);
    }

    public void leave(String username, Session session) throws IOException {
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

    public void connect(String username, Session session) throws IOException, ResponseException {

        connections.add(username, gameID, session);

        GameData game = new MySqlGameDataAccess().getGame(gameID);
        ChessGame chess = game.game();
        LoadGameMessage load = new LoadGameMessage(chess);
        session.getRemote().sendString(new Gson().toJson(load));

        var message = String.format("%s entered the game!", username);
        var notification = new GameNotificationMessage(message);
        connections.broadcast(gameID, session, notification);
    }
}
