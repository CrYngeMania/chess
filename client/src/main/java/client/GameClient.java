package client;

import chess.ChessGame;
import chess.ChessPiece;
import facade.ServerFacade;

import java.io.PrintStream;
import java.util.Objects;

import static ui.EscapeSequences.*;

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
        var out = new PrintStream(System.out, true);
            printBoard(out);
    }

    public static final int BOARD_SIZE_IN_SQUARES = 8;
    public static final int SQUARE_SIZE_IN_PADDED_CHARS = 1;


    private void drawHeaders(PrintStream out) {

        String[] headers = {"a", "b", "c", "d", "e", "f", "g", "h"};

        out.print("   ");

        if (Objects.equals(playerType, "WHITE") || Objects.equals(playerType, "OBSERVER")) {
            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; boardCol++) {
                drawHeader(out, headers[boardCol]);
            }
        }
        else {
            for (int boardCol = BOARD_SIZE_IN_SQUARES - 1; boardCol >= 0; boardCol--) {
            drawHeader(out, headers[boardCol]);
            }
        }
    }

    private static void drawHeader(PrintStream out, String header) {

        out.print(" ");
        printHeaderText(out, header);
        out.print("  ");
    }

    private static void printHeaderText(PrintStream out, String header) {
        out.print(header);
    }

    private static void drawBoard(PrintStream out) {
        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; boardRow++){
            drawRowOfSquares(out, boardRow);
        }
    }

    private static void drawRowOfSquares(PrintStream out, int boardRow) {

        int displayRow = BOARD_SIZE_IN_SQUARES - boardRow;
        out.print(displayRow + " ");


        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; boardCol++) {
            if ((boardRow + boardCol) % 2 == 0){
                out.print(SET_BG_COLOR_LIGHT_GREY);
            } else {out.print(SET_BG_COLOR_DARK_GREY);}

            out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));

            }

        out.print(RESET_BG_COLOR);
        out.println();
    }


    private static void printPlayer(PrintStream out, ChessPiece.PieceType piece){
    }

    public void printBoard(PrintStream out){
        drawHeaders(out);
        out.println();
        drawBoard(out);
    }
}
