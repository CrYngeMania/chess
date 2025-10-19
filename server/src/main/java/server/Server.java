package server;

import com.google.gson.Gson;
import model.UserData;
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

        server.delete("db", ctx -> ctx.result("{}"));

        server.post("user", this::register);

        // Register your endpoints and exception handlers here.

    }

    private void register(Context ctx){
        var serialiser = new Gson();
        var req = serialiser.fromJson(ctx.body(), UserData.class);
        var res = serialiser.toJson(req);

        var response = userService.register(req);
        ctx.result(serialiser.toJson(response));

    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
