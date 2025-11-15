import chess.*;
import client.PreLoginClient;
import dataaccess.DataAccessException;
import server.Server;
import client.PreLoginClient;

public class Main {

    public static void main(String[] args) {
        String serverUrl = "http://localhost:8080";
        new PreLoginClient(serverUrl).run();
    }
}