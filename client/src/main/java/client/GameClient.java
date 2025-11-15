package client;

import chess.ChessGame;
import facade.ServerFacade;
import ui.EscapeSequences;

import java.util.Objects;

import static ui.EscapeSequences.SET_BG_COLOR_DARK_GREY;
import static ui.EscapeSequences.SET_BG_COLOR_LIGHT_GREY;

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
            printBoard();
    }

    public void printBoard(){
        boolean isWhite = !Objects.equals(playerType, "BLACK");

        int start = 8; int end = 1; int step = -1;
        if (isWhite){
            start = 1;
            end = 8;
            step = 1;
        }

        int[] cols;
        if(isWhite){
            cols = new int[]{1,2,3,4,5,6,7,8};
        }
        else{
            cols = new int[]{8,7,6,5,4,3,2,1};
        }

        for (int row = start; row <= end; row+=step){
            System.out.print(row + " ");

            for (int col:cols) {
                boolean isLight = ((row + col) % 2 != 0);

                if (isLight) {
                    System.out.print(SET_BG_COLOR_LIGHT_GREY);
                }
                else{
                    System.out.print(SET_BG_COLOR_DARK_GREY);
                }
            }
            System.out.println(" " + row);
        }






    }
}
