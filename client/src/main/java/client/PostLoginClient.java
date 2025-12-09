package client;

import chess.ChessGame;
import exception.ResponseException;
import facade.ServerFacade;
import model.GameData;

import java.util.*;

public class PostLoginClient {
    private final ServerFacade server;
    private List<Map<String, Object>> currentGames;
    private final String url;

    public PostLoginClient(ServerFacade server, String url) {
        this.url = url;
        this.server = server;
        currentGames = new ArrayList<>();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!result.equals("Logging out!")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = evaluate(line);
                System.out.print(result);
            } catch (Throwable e){
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public String evaluate(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "help" -> help();
                case "create" -> create(params);
                case "join" -> join(params);
                case "list" -> list();
                case "observe" -> observe(params);
                case "logout" -> logout();
                default -> "That's not a valid command, you silly goober";
            };

        } catch (ResponseException ex) {
            return ex.getMessage();
        }

    }

    private void printPrompt() {
        System.out.print("\n" + "LOGGED_IN " + ">>> ");}

    public String help() {
        return """
                    create <NAME> - creates a game with the given name
                    list - shows all games
                    join <ID> [WHITE|BLACK] - join a game
                    * When joining a game, make sure the color you want is open!
                    observe <ID> - a game
                    logout - logs out the user
                    help - shows possible commands""";
    }

    public String create(String... params) throws ResponseException{
        if (params.length >= 1) {
            String gameName = params[0];

            server.createGame(gameName);
            return String.format("Game %s created!", gameName);
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Error: Expected <NAME>");
    }

    public List<Map<String, Object>> listGames(HashMap<String, Object> gameResponse) {
        List<Map<String, Object>> prettyList = new ArrayList<>();

        ArrayList<GameData> games = (ArrayList<GameData>) gameResponse.get("games");

        for (int i = 0; i< games.size(); i++){
            GameData game = games.get(i);
            Map<String, Object> summary = new HashMap<>();

            summary.put("number", i + 1);
            summary.put("gameID", game.gameID());
            summary.put("gameName", game.gameName());
            summary.put("whiteUsername", game.whiteUsername());
            summary.put("blackUsername", game.blackUsername());
            summary.put("game", game.game());

            prettyList.add(summary);

        }
        currentGames = prettyList;
        return currentGames;
    }

    public String list() throws ResponseException{
        HashMap<String, Object> result = server.listGame();

        StringBuilder builder = new StringBuilder();
        var listToPrint = listGames(result);
        for (var game: listToPrint){
            builder.append(game.get("number"))
                    .append(". ")
                    .append(game.get("gameName"))
                    .append(", White Player(")
                    .append(game.get("whiteUsername"))
                    .append("), Black Player(")
                    .append(game.get("blackUsername"))
                    .append(")\n");
        }

        if (listToPrint.isEmpty()){
            return "No games! Maybe you should make one :)";
        }
        return builder.toString();
    }

    public String join(String... params) throws ResponseException{
        if (params.length >= 2) {
            int gameNumber;
            String playerColor = params[1].toUpperCase();
                try{
                    gameNumber = Integer.parseInt(params[0]);}
                catch (Exception e){
                    throw new ResponseException(ResponseException.Code.ClientError, "Error: I need the number of the game, silly :)");
                }
                if (currentGames.isEmpty()){
                    throw new ResponseException(ResponseException.Code.ClientError, "Error: Make sure you list the games first!");
                }
                if (gameNumber < 1 || gameNumber > currentGames.size()){
                    throw new ResponseException(ResponseException.Code.ClientError, "Error: I need a valid game number, player ;)");
                }

                var wantedGame = currentGames.get(gameNumber - 1);
                int gameID = (Integer) wantedGame.get("gameID");
                server.joinGame(playerColor, gameID);
                runGame(playerColor, (ChessGame) wantedGame.get("game"), gameID);

                server.leaveGame(gameID);

                return("");
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Error: Expected <ID> [WHITE|BLACK]");
    }

    public String observe(String... params) throws ResponseException{
        if (params.length >= 1) {
            int gameNumber;

            try{
                gameNumber = Integer.parseInt(params[0]);}
            catch (Exception e){
                throw new ResponseException(ResponseException.Code.ClientError, "Error: I need the number of the game, silly :)");
            }
            if (currentGames.isEmpty()){
                throw new ResponseException(ResponseException.Code.ClientError, "Error: Make sure you list the games first!");
            }
            if (gameNumber < 1 || gameNumber > currentGames.size()){
                throw new ResponseException(ResponseException.Code.ClientError, "Error: I need a valid game number, player ;)");
            }

            var wantedGame = currentGames.get(gameNumber - 1);
            int gameID = (Integer) wantedGame.get("gameID");
            runGame("OBSERVER", (ChessGame) wantedGame.get("game"), gameID);
            return "";
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Error: Expected <ID>");
    }

    private void runGame(String playerType, ChessGame game, Integer gameID) {
        GameClient client = new GameClient(server, playerType, game, gameID, url);
        try{
            client.run();
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }

    }

    public String logout() throws ResponseException{
        server.logout();
        return "Logging out!";
    }

}
