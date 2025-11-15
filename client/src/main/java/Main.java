import chess.*;
import client.PreLoginClient;
import dataaccess.DataAccessException;
import server.Server;

public class Main {

    }

    public static void main(String[] args) {

        Server server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        String serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }

        new PreLoginClient(serverUrl).run();
    }