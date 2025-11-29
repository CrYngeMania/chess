package client;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import facade.ServerFacade;

import java.io.PrintStream;
import java.util.Objects;

import static chess.ChessPiece.*;
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

    public String help() {
        return """
                    redraw - redraws the current board
                    leave - leave the game
                    makeMove <move> - moves your piece
                    resign - admit defeat and leave the game
                    highlight - shows all your legal moves
                    help - shows possible commands""";
    }



    public static final int BOARD_SIZE_IN_SQUARES = 8;
    public static final int SQUARE_SIZE_IN_PADDED_CHARS = 1;


    private void drawHeaders(PrintStream out) {

        String[] headers = {"a", "b", "c", "d", "e", "f", "g", "h"};

        out.print(" ");

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

    private void drawBoard(PrintStream out) {
        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; boardRow++){
            drawRowOfSquares(out, boardRow);
        }
    }

    private void drawRowOfSquares(PrintStream out, int boardRow) {

        int displayRow = BOARD_SIZE_IN_SQUARES - boardRow;;
        if (Objects.equals("BLACK", playerType)) {
            displayRow = 1 + boardRow;
        }

        out.print(displayRow + " ");


        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; boardCol++) {
            if ((boardRow + boardCol) % 2 == 0){
                out.print(SET_BG_COLOR_LIGHT_GREY);
            } else {out.print(SET_BG_COLOR_DARK_GREY);}

            if (Objects.equals("BLACK", playerType)) {
                ChessPiece piece = game.getBoard().getPiece(new ChessPosition(boardRow + 1, BOARD_SIZE_IN_SQUARES - boardCol));
                if (piece == null){
                    out.print(EMPTY);
                }
                else{
                    printPiece(out, piece);
                }
            }
            else{
                ChessPiece piece = game.getBoard().getPiece(new ChessPosition( BOARD_SIZE_IN_SQUARES -  boardRow, boardCol + 1));
                if (piece == null){
                    out.print(EMPTY);
                }
                else{
                    printPiece(out, piece);
                }
            }
            }



        out.print(RESET_BG_COLOR);
        out.print(" " + displayRow);
        out.println();
    }

    private void printPiece(PrintStream out, ChessPiece piece) {
        ChessGame.TeamColor color = piece.getTeamColor();
        if (Objects.equals(ChessGame.TeamColor.BLACK, color)){
            printBlack(out, piece.getPieceType());
        }
        else{printWhite(out, piece.getPieceType());}
    }

    private void printBlack(PrintStream out, PieceType type) {
        switch (type){
            case PieceType.PAWN -> {
                out.print(SET_TEXT_COLOR_RED);
                out.print(BLACK_PAWN);
                out.print(RESET_TEXT_COLOR);
            }
            case PieceType.ROOK -> {
                out.print(SET_TEXT_COLOR_RED);
                out.print(BLACK_ROOK);
                out.print(RESET_TEXT_COLOR);
            }
            case PieceType.BISHOP -> {
                out.print(SET_TEXT_COLOR_RED);
                out.print(BLACK_BISHOP);
                out.print(RESET_TEXT_COLOR);
            }
            case PieceType.KNIGHT -> {
                out.print(SET_TEXT_COLOR_RED);
                out.print(BLACK_KNIGHT);
                out.print(RESET_TEXT_COLOR);
            }
            case PieceType.QUEEN -> {
                out.print(SET_TEXT_COLOR_RED);
                out.print(BLACK_QUEEN);
                out.print(RESET_TEXT_COLOR);
            }
            case PieceType.KING -> {
                out.print(SET_TEXT_COLOR_RED);
                out.print(BLACK_KING);
                out.print(RESET_TEXT_COLOR);
            }
        }
    }

    private void printWhite(PrintStream out, PieceType type) {
        switch (type){
            case PieceType.PAWN -> {
                out.print(SET_TEXT_COLOR_WHITE);
                out.print(WHITE_PAWN);
                out.print(RESET_TEXT_COLOR);
            }
            case PieceType.ROOK -> {
                out.print(SET_TEXT_COLOR_WHITE);
                out.print(WHITE_ROOK);
                out.print(RESET_TEXT_COLOR);
            }
            case PieceType.BISHOP -> {
                out.print(SET_TEXT_COLOR_WHITE);
                out.print(WHITE_BISHOP);
                out.print(RESET_TEXT_COLOR);
            }
            case PieceType.KNIGHT -> {
                out.print(SET_TEXT_COLOR_WHITE);
                out.print(WHITE_KNIGHT);
                out.print(RESET_TEXT_COLOR);
            }
            case PieceType.QUEEN -> {
                out.print(SET_TEXT_COLOR_WHITE);
                out.print(WHITE_QUEEN);
                out.print(RESET_TEXT_COLOR);
            }
            case PieceType.KING -> {
                out.print(SET_TEXT_COLOR_WHITE);
                out.print(WHITE_KING);
                out.print(RESET_TEXT_COLOR);
            }
        }
    }

    public void printBoard(PrintStream out){
        drawHeaders(out);
        out.println();
        drawBoard(out);
        drawHeaders(out);
    }
}


