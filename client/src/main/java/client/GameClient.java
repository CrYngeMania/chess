package client;

import chess.ChessGame;
import facade.ServerFacade;
import ui.EscapeSequences;

import java.util.Objects;

public class GameClient {
    private final ServerFacade server;
    String playerType;
    ChessGame game;

    public GameClient(ServerFacade server, String playerType, ChessGame game) {
        this.server = server;
        this.playerType = playerType;
        this.game = game;
    }

    public void run() {
        if (Objects.equals(playerType, "BLACK")) {
            System.out.print("im black");
        }
        else {
            System.out.print("i can see white");
        }
    }

    public void printBoard(){
        boolean isWhite = !Objects.equals(playerType, "BLACK");

        int start = 8; int end = 1; int step = -1;
        if (isWhite){
            start = 1;
            end = 8;
            step = 1;
        }




    }
}
