package client;

import java.util.Scanner;

public class ChessClient {
    private boolean loggedIn;

    public void run() {
        System.out.println("Welcome to 240 Chess! Type help to get started :D\n");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = evaluate(line);
            } catch (Throwable e){

            }
        }

    }

    private void printPrompt() {
        String loginState;
        if (!loggedIn){
            loginState = "LOGGED OUT";
        }
        else {
            loginState = "LOGGED IN";
        }

        System.out.print("\n" + loginState + ">>>");}

    public void help() {
        if (!loggedIn){
            System.out.println("""
                    register <USERNAME> <PASSWORD> <EMAIL> - create a new account
                    
                    login <USERNAME> <PASSWORD> - log in to play chess
                    
                    quit - exit
                    
                    help - shows possible commands""");
        }
        else{
            System.out.println("""
                    create game <GAME NAME> - creates a game with the given name
                    
                    list - shows all games
                    
                    join <ID> [WHITE|BLACK] - join game with given color
                    
                    observe <ID> - join game as an observer
                    
                    logout - logs out of program
                    
                    quit - exit a game
                    
                    help - shows possible commands""");
        }
    }
}
