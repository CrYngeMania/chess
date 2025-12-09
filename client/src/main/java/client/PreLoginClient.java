package client;

import exception.ResponseException;
import facade.ServerFacade;

import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class PreLoginClient {
    private final ServerFacade server;
    private final String url;

    public PreLoginClient(String url) {
        this.url = url;
        server = new ServerFacade(url);
    }


    public void run() {
        System.out.println("Welcome to CS 240 Chess! Type help to get started :D");
        Scanner scanner = new Scanner(System.in);
        String result = "";
        while(!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = evaluate(line);
                if(!result.equals("quit")){
                    System.out.print(result);
                }
            } catch (Throwable e){
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
        System.exit(200);
    }

    public String evaluate(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "help" -> help();
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                case "delete" -> delete();
                default -> "That's not a valid command, you silly goober";
            };

        } catch (ResponseException ex) {
            return ex.getMessage();
        }

    }
    public String delete() throws ResponseException {
        server.delete();
        return "deleted";
    }

    private void printPrompt() {
        System.out.print("\n" + "LOGGED_OUT " + ">>> ");}

    public String help() {
            return """
                    register <USERNAME> <PASSWORD> <EMAIL> - create a new account
                    login <USERNAME> <PASSWORD> - log in to play chess
                    quit - exit
                    help - shows possible commands""";

    }

    public String register(String... params) throws ResponseException {
        if (params.length >= 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];

            server.register(username, password, email);
            System.out.printf("You're registered as %s! Welcome to the game :)", username);
            runPost();
            return "";

        }
        throw new ResponseException(ResponseException.Code.ClientError, "Error: Expected <USERNAME> <PASSWORD> <EMAIL>");
    }

    private void runPost() {
        PostLoginClient client = new PostLoginClient(server, url);
        client.run();
    }

    public String login(String... params) throws ResponseException {
        if (params.length >= 2) {
            String username = params[0];
            String password = params[1];

            if (Objects.equals(username, "jay")) {
                System.out.println("jay, you're here quite a lot.....");
                System.out.println("Maybe you should take a break from chess.....");
            }

            server.login(username, password);
            System.out.printf("Welcome back, %s! You're logged in!", username);
            runPost();
            return "";
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Error: Expected <USERNAME> <PASSWORD>");
    }

}
