package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.UserData;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import service.UserService;
import java.util.UUID;


public class Server {

    private final Javalin server;
    private UserService userService;
    private DataAccess dataAccess;

    public Server() {

        dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);

        server = Javalin.create(config -> config.staticFiles.add("web"));

        server.delete("db", ctx -> ctx.result("{}"));

        server.post("user", this::register);

        // Register your endpoints and exception handlers here.

    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    private void register(Context ctx){
        var serialiser = new Gson();
        try {
            var req = serialiser.fromJson(ctx.body(), UserData.class);
            var res = serialiser.toJson(req);
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

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}

record ErrorResponse(String message) {}