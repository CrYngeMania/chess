package server;

import com.google.gson.Gson;
import datamodel.*;
import dataaccess.*;
import exception.ResponseException;
import io.javalin.*;
import io.javalin.http.Context;
import server.websocket.WebSocketHandler;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin server;
    private final UserService userService;
    private final GameService gameService;
    private final WebSocketHandler handler;

    public Server() {

        MySqlUserDataAccess dataAccess = new MySqlUserDataAccess();
        GameDataAccess gameDataAccess = new MySqlGameDataAccess();
        AuthDataAccess authDataAccess = new MySqlAuthDataAccess();

        this.handler = new WebSocketHandler();

        try {
            dataAccess.configureDatabase();
        } catch (DataAccessException | ResponseException ex) {
            throw new RuntimeException("Error: Database compilation error");
        }

        userService = new UserService(dataAccess, gameDataAccess, authDataAccess);
        gameService = new GameService(gameDataAccess, authDataAccess);

        server = Javalin.create(config -> config.staticFiles.add("web"));

        server.delete("db", this::delete);

        server.post("user", this::register);

        server.post("session", this::login);

        server.delete("session", this::logout);

        server.post("game", this::createGame);

        server.get("game", this::listGames);

        server.put("game", this::joinGame);

        server.delete("game", this::leaveGame);

        server.ws("/ws", ws -> {
            ws.onConnect(ctx -> {
                ctx.enableAutomaticPings();
                System.out.println("Websocket connected");
            });
            ws.onMessage(ctx -> {
                System.out.println("message received");
                ctx.send("WebSocket response:" + ctx.message());
                System.out.println("message sent");
            });
            ws.onClose(_ -> System.out.println("Websocket closed"));
        });

//        server.ws("/ws", ws -> {
//            ws.onConnect(handler);
//            ws.onMessage(handler);
//            ws.onClose(handler);
//        })
        ;

        // Register your endpoints and exception handlers here.

    }

    public void dataErrors(Context ctx, Gson serialiser, ResponseException ex){
        ctx.status(ex.toHttpStatusCode());
        ctx.result(serialiser.toJson(
                new ErrorResponse("Error:" + ex.getMessage())
        ));
    }
    public void errors(Context ctx, Gson serialiser, Exception ex){
        ctx.status(500);
        ctx.result(serialiser.toJson(
                new ErrorResponse("Error:" + ex.getMessage())
        ));
    }


    public void register(Context ctx){
        var serialiser = new Gson();
        try {
            var req = serialiser.fromJson(ctx.body(), RegistrationRequest.class);
            var response = userService.register(req);
            ctx.result(serialiser.toJson(response));
        }
        catch (ResponseException ex){
            dataErrors(ctx, serialiser, ex);
        }
        catch (Exception ex){
            errors(ctx, serialiser, ex);

        }

    }

    private void login(Context ctx){
        var serialiser = new Gson();
        try {
            var req = serialiser.fromJson(ctx.body(), LoginRequest.class);
            var response = userService.login(req);
            ctx.result(serialiser.toJson(response));
        }
        catch (ResponseException ex){
            dataErrors(ctx, serialiser, ex);
        }
        catch (Exception ex){
            errors(ctx, serialiser, ex);
        }

    }

    private void logout(Context ctx){
        var serialiser = new Gson();
        try {
            var req = ctx.header("Authorization");
            var response = userService.logout(req);
            ctx.result(serialiser.toJson(response));
        }
        catch (ResponseException ex){
            dataErrors(ctx, serialiser, ex);
        }
        catch (Exception ex){
            errors(ctx, serialiser, ex);

        }

    }

    private void createGame(Context ctx){
        var serialiser = new Gson();
        try {
            var req = serialiser.fromJson(ctx.body(), CreateGameRequest.class);
            var token = ctx.header("Authorization");
            var response = gameService.createGame(req, token);
            ctx.result(serialiser.toJson(response));
        }
        catch (ResponseException ex){
            dataErrors(ctx, serialiser, ex);
        }
        catch (Exception ex){
            errors(ctx, serialiser, ex);

        }

    }

    public void joinGame(Context ctx) {
        var serialiser = new Gson();
        try {
            var req = serialiser.fromJson(ctx.body(), JoinGameRequest.class);
            var token = ctx.header("Authorization");
            var response = gameService.joinGame(req, token);
            ctx.result(serialiser.toJson(response));
        }
        catch (ResponseException ex){
            dataErrors(ctx, serialiser, ex);
        }
        catch (Exception ex){
            errors(ctx, serialiser, ex);
        }
    }

    public void leaveGame(Context ctx) {
        var serialiser = new Gson();
        try {
            var req = serialiser.fromJson(ctx.body(), LeaveGameRequest.class);
            var token = ctx.header("Authorization");
            var response = gameService.leaveGame(req, token);
            ctx.result(serialiser.toJson(response));
        }
        catch (ResponseException ex){
            dataErrors(ctx, serialiser, ex);
        }
        catch (Exception ex){
            errors(ctx, serialiser, ex);
        }
    }


    public void listGames(Context ctx){
        var serialiser = new Gson();
        try {
            var req = ctx.header("Authorization");
            var response = gameService.listGame(req);
            ctx.result(serialiser.toJson(response));
        }
        catch (ResponseException ex){
            dataErrors(ctx, serialiser, ex);
        }
        catch (Exception ex){
            errors(ctx, serialiser, ex);

        }

    }

    public void delete(Context ctx){
        var serialiser = new Gson();
        try {
            var req = ctx.header("Authorization");
            var response = userService.delete(req);
            ctx.result(serialiser.toJson(response));
        }
        catch (ResponseException ex){
            dataErrors(ctx, serialiser, ex);
        }
        catch (Exception ex){
            errors(ctx, serialiser, ex);
        }

    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}

record ErrorResponse(String message) {}