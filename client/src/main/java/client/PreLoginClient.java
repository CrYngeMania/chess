package client;

import java.util.Scanner;

public class PreLoginClient {
    boolean loggedIn = false;

    public void run() {
        System.out.println("Welcome to 240 Chess! Type help to get started :D\n");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = line;
                System.out.print(result);
            } catch (Throwable e){
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {

        System.out.print("\n" + "LOGGED_IN" + ">>>");}

    public void help() {
            System.out.println("""
                    register <USERNAME> <PASSWORD> <EMAIL> - create a new account
                    
                    login <USERNAME> <PASSWORD> - log in to play chess
                    
                    quit - exit
                    
                    help - shows possible commands""");

    }

    public String register(String... params) throws Exception {
        if (params.length >= 3) {
            loggedIn = true;
            String username = params[0];
            String password = params[1];
            String email = params[2];

        }
        return "";
    }
}
