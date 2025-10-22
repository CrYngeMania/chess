package server;

import com.google.gson.Gson;
import dataModel.*;
import dataaccess.DataAccessException;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import service.UserService;



public class Server {

    private final Javalin server;
    private UserService userService;
    private DataAccess dataAccess;

    public Server() {

        dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);

        server = Javalin.create(config -> config.staticFiles.add("web"));

        server.delete("db", this::delete);

        server.post("user", this::register);

        server.post("session", this::login);

        server.delete("session", this::logout);

        server.post("game", this::createGame);

        server.get("game", this::listGames);

        server.put("game", this::joinGame);

        // Register your endpoints and exception handlers here.

    }



    private void register(Context ctx){
        var serialiser = new Gson();
        try {
            var req = serialiser.fromJson(ctx.body(), RegistrationRequest.class);
            var response = userService.register(req);
            ctx.result(serialiser.toJson(response));
        }
        catch (DataAccessException ex){
            ctx.status(ex.toHttpStatusCode());
            ctx.result(serialiser.toJson(
                    new ErrorResponse(ex.getMessage())
            ));
        }
        catch (Exception ex){
            ctx.status(500);
            ctx.result(serialiser.toJson(
                    new ErrorResponse(ex.getMessage())
            ));

        }

    }

    private void login(Context ctx){
        var serialiser = new Gson();
        try {
            var req = serialiser.fromJson(ctx.body(), LoginRequest.class);
            var response = userService.login(req);
            ctx.result(serialiser.toJson(response));
        }
        catch (DataAccessException ex){
            ctx.status(ex.toHttpStatusCode());
            ctx.result(serialiser.toJson(
                    new ErrorResponse(ex.getMessage())
            ));
        }
        catch (Exception ex){
            ctx.status(500);
            ctx.result(serialiser.toJson(
                    new ErrorResponse(ex.getMessage())
            ));

        }

    }

    private void logout(Context ctx){
        var serialiser = new Gson();
        try {
            var req = ctx.header("Authorization");
            var response = userService.logout(req);
            ctx.result(serialiser.toJson(response));
        }
        catch (DataAccessException ex){
            ctx.status(ex.toHttpStatusCode());
            ctx.result(serialiser.toJson(
                    new ErrorResponse(ex.getMessage())
            ));
        }
        catch (Exception ex){
            ctx.status(500);
            ctx.result(serialiser.toJson(
                    new ErrorResponse(ex.getMessage())
            ));

        }

    }

    private void createGame(Context ctx){
        var serialiser = new Gson();
        try {
            var req = serialiser.fromJson(ctx.body(), CreateGameRequest.class);
            var token = ctx.header("Authorization");
            var response = userService.createGame(req, token);
            ctx.result(serialiser.toJson(response));
        }
        catch (DataAccessException ex){
            ctx.status(ex.toHttpStatusCode());
            ctx.result(serialiser.toJson(
                    new ErrorResponse(ex.getMessage())
            ));
        }
        catch (Exception ex){
            ctx.status(500);
            ctx.result(serialiser.toJson(
                    new ErrorResponse(ex.getMessage())
            ));

        }

    }

    public void joinGame(Context ctx) {
        var serialiser = new Gson();
        try {
            var req = serialiser.fromJson(ctx.body(), JoinGameRequest.class);
            var token = ctx.header("Authorization");
            var response = userService.joinGame(req, token);
            ctx.result(serialiser.toJson(response));
        }
        catch (DataAccessException ex){
            ctx.status(ex.toHttpStatusCode());
            ctx.result(serialiser.toJson(
                    new ErrorResponse(ex.getMessage())
            ));
        }
        catch (Exception ex){
            ctx.status(500);
            ctx.result(serialiser.toJson(
                    new ErrorResponse(ex.getMessage())
            ));

        }

    }


    public void listGames(Context ctx){
        var serialiser = new Gson();
        try {
            var req = ctx.header("Authorization");
            var response = userService.listGame(req);
            ctx.result(serialiser.toJson(response));
        }
        catch (DataAccessException ex){
            ctx.status(ex.toHttpStatusCode());
            ctx.result(serialiser.toJson(
                    new ErrorResponse(ex.getMessage())
            ));
        }
        catch (Exception ex){
            ctx.status(500);
            ctx.result(serialiser.toJson(
                    new ErrorResponse(ex.getMessage())
            ));

        }

    }

    public void delete(Context ctx){
        var serialiser = new Gson();
        try {
            var req = ctx.header("Authorization");
            var response = userService.delete(req);
            ctx.result(serialiser.toJson(response));
        }
        catch (DataAccessException ex){
            ctx.status(ex.toHttpStatusCode());
            ctx.result(serialiser.toJson(
                    new ErrorResponse(ex.getMessage())
            ));
        }
        catch (Exception ex){
            ctx.status(500);
            ctx.result(serialiser.toJson(
                    new ErrorResponse(ex.getMessage())
            ));

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