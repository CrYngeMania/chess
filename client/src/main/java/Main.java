import chess.*;
import client.PreLoginClient;

public class Main {
    static String serverUrl = "http://localhost:8080";
    static PreLoginClient client = new PreLoginClient(serverUrl);

    public static void main(String[] args) {
        client.run();
    }
}