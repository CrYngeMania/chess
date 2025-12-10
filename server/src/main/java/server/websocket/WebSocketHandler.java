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

    WsMessageContext context;


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
        context = ctx;
        Gson gson = new Gson();
        try{
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

        ChessGame.TeamColor player;
        String colorUsername;

        GameData game = new MySqlGameDataAccess().getGame(gameID);

        if (Objects.equals(username, game.whiteUsername())){
            player = ChessGame.TeamColor.WHITE;
        }
        else if (Objects.equals(username, game.blackUsername())) {
            player = ChessGame.TeamColor.BLACK;
        }
        else{
            ErrorMessage msg = new ErrorMessage("You aren't even playing!");
            context.session.getRemote().sendString(new Gson().toJson(msg));
            return;
        }

        if (Objects.equals(username, game.blackUsername())){
            color = ChessGame.TeamColor.WHITE;
            colorUsername = game.whiteUsername();
        }
        else{
            color = ChessGame.TeamColor.BLACK;
            colorUsername = game.blackUsername();
        }

        if (game.game().isInCheckmate(color) || game.game().isInStalemate(color)){
            ErrorMessage msg = new ErrorMessage("The game's done! You can go home now :)");
            context.session.getRemote().sendString(new Gson().toJson(msg));
            return;
        }


        if (!Objects.equals(player, game.game().getTeamTurn())){
            ErrorMessage msg = new ErrorMessage("Hey! Not your turn, buddy boy");
            context.session.getRemote().sendString(new Gson().toJson(msg));
        }

        else{
            ChessGame newGame = game.game();

            if (game.game().hasResigned()){
                ErrorMessage msg = new ErrorMessage("The game's over, pal :/");
                context.session.getRemote().sendString(new Gson().toJson(msg));
                return;
            }

            if (game.game().isInCheckmate(color)){
                var message = String.format("%s wins! What a champ! %s is caught in a checkmate!", username, colorUsername);
                var notification = new GameNotificationMessage(message);
                connections.broadcast(gameID, null, notification);
            }

            newGame.makeMove(moveCommand.getMove());
            GameData newGameData = new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), newGame);
            gameDAO.updateGame(game.gameID(), newGameData);

            LoadGameMessage update = new LoadGameMessage(game.game());
            connections.broadcast(gameID, null, update);

            var initMessage = String.format("%s made a move (%s)!", username, moveCommand.getMove());
            var initNotification = new GameNotificationMessage(initMessage);
            connections.broadcast(gameID, session, initNotification);

            if (game.game().isInCheckmate(color)){
                var message = String.format("%s wins! What a champ! %s is caught in a checkmate!", username, colorUsername);
                var notification = new GameNotificationMessage(message);
                connections.broadcast(gameID, null, notification);
            }
            else if(game.game().isInStalemate(color)){
                var message = "Oh dear..... looks like a stalemate :(";
                var notification = new GameNotificationMessage(message);
                connections.broadcast(gameID, null, notification);
            }
            else if (game.game().isInCheck(color)){
                var message = String.format("%s is in check! Might want to fix that...", colorUsername);
                var notification = new GameNotificationMessage(message);
                connections.broadcast(gameID, null, notification);
            }
        }

    }

    public void resign(String authToken, Integer gameID, Session session) throws IOException, ResponseException {
        String username = getUsername(authToken);
        ChessGame.TeamColor color;

        GameData game = new MySqlGameDataAccess().getGame(gameID);

        if (game.game().hasResigned()){
            ErrorMessage msg = new ErrorMessage("Someone already resigned, you silly goober");
            context.session.getRemote().sendString(new Gson().toJson(msg));
            return;
        }

        if (Objects.equals(username, game.blackUsername())){
            color = ChessGame.TeamColor.BLACK;
        }
        else if (Objects.equals(username, game.whiteUsername())){
        color = ChessGame.TeamColor.WHITE;}
        else{
            color = null;
            ErrorMessage msg = new ErrorMessage("You aren't even in this game!");
            context.session.getRemote().sendString(new Gson().toJson(msg));
            return;
        }

        ChessGame newGame = game.game();
        newGame.resign(color);
        GameData newGameData = new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), newGame);
        gameDAO.updateGame(game.gameID(), newGameData);


        var message = String.format("%s resigned!", username);
        var notification = new GameNotificationMessage(message);
        connections.broadcast(gameID, null, notification);
    }

    public void leave(String authToken, Integer gameID, Session session) throws IOException, ResponseException {
        String username = getUsername(authToken);

        GameData game = gameDAO.getGame(gameID);

        if (Objects.equals(username, game.whiteUsername())) {
            game = new GameData(game.gameID(), null, game.blackUsername(), game.gameName(), game.game());
        } else if (Objects.equals(username, game.blackUsername())) {
            game = new GameData(game.gameID(), game.whiteUsername(), null, game.gameName(), game.game());
        }

        gameDAO.updateGame(gameID, game);

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
        String type;

        connections.add(username, gameID, session);
        System.out.println("Websocket connected");

        GameData game = new MySqlGameDataAccess().getGame(gameID);

        if (Objects.equals(username, game.blackUsername())){
            type = "Black";
        }
        else if (Objects.equals(username, game.whiteUsername())){
            type = "White";
        }
        else{
            type = "Observer";
        }

        ChessGame chess = game.game();
        LoadGameMessage load = new LoadGameMessage(chess);
        session.getRemote().sendString(new Gson().toJson(load));

        var notification = new GameNotificationMessage(username + " entered the game as " + type + "!");
        connections.broadcast(gameID, session, notification);
    }
}
