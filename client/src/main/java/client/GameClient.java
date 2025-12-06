package client;

import chess.*;
import exception.ResponseException;
import facade.ServerFacade;

import java.io.PrintStream;
import java.util.*;

import static chess.ChessPiece.*;
import static ui.EscapeSequences.*;

public class GameClient {
    private final ServerFacade server;
    String playerType;
    ChessGame game;
    PrintStream out = new PrintStream(System.out, true);
    Boolean gameComplete = false;


    public GameClient(ServerFacade server, String playerType, ChessGame game) {
        this.server = server;
        this.playerType = playerType;
        this.game = game;
    }

    public void run() {
        printBoard(out, null);
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!result.equals("Leaving!")) {
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

    public String evaluate(String input) throws ResponseException {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "help" -> help();
                case "redraw" -> reprintBoard(out);
                case "leave" -> "Leaving!";
                case "move" -> makeMove(out, params);
                case "resign" -> resign();
                case "highlight" -> highlightBoard(out, params);
                default -> "That's not a valid command, you silly goober";
            };
        }
        catch (ResponseException | InvalidMoveException ex) {
            return ex.getMessage();
        }
    }

    public Integer getColumn(String row) throws ResponseException {
        return switch(row){
            case "a" -> 1;
            case "b" -> 2;
            case "c" -> 3;
            case "d" -> 4;
            case "e" -> 5;
            case "f" -> 6;
            case "g" -> 7;
            case "h" -> 8;
            default -> throw new ResponseException(ResponseException.Code.ClientError,"That's not a valid row!");
        };
    }

    public String highlightBoard(PrintStream out, String... params) throws ResponseException {
        String rowString = params[0].substring(0, 1);
        int row = getColumn(rowString);
        int col = Integer.parseInt(params[0].substring(1));
        Collection<ChessMove> validMoves = game.validMoves(new ChessPosition(col, row));
        printBoard(out, validMoves);
        return "";
    }

    public String reprintBoard(PrintStream out) {
        printBoard(out, null);
        return "";
    }

    public String help() {
        return """
                    redraw - redraws the current board
                    leave - leave the game
                    move <move> - moves your piece
                    resign - admit defeat and leave the game
                    highlight <move> - shows all piece legal moves
                    help - shows possible commands""";
    }

    public String resign() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Are you sure you want to resign? y/n\n");
        String answer = scanner.nextLine();
        if (Objects.equals(answer, "y")){
            gameComplete = true;
            return "Better luck next time!";
        }
        else {
            return "Good luck!";
        }
    }

    public String makeMove(PrintStream out, String... params) throws ResponseException, InvalidMoveException {
        if (Objects.equals(playerType, "OBSERVER")){
            return "You can't make a move, you silly goober";
        }
        String startRowString = params[0].substring(0, 1);
        int startRow = getColumn(startRowString);
        int startCol = Integer.parseInt(params[0].substring(1));
        ChessPosition start = new ChessPosition(startCol, startRow);
        String endRowString = params[1].substring(0, 1);
        int endRow = getColumn(endRowString);
        int endCol = Integer.parseInt(params[1].substring(1));
        ChessPosition end = new ChessPosition(endCol, endRow);

        ChessMove move = new ChessMove(start, end, null);
        game.makeMove(move);
        printBoard(out, null);
        return "";
    }

    private void printPrompt() {
        System.out.print("\n" + "GAME " + ">>> ");}

    public static final int BOARD_SIZE_IN_SQUARES = 8;

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

    private void drawBoard(PrintStream out, Collection<ChessMove> highlights) {
        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; boardRow++){
            drawRowOfSquares(out, boardRow, highlights);
        }
    }

    private void drawRowOfSquares(PrintStream out, int boardRow, Collection<ChessMove> highlights) {

        int displayRow = BOARD_SIZE_IN_SQUARES - boardRow;;
        if (Objects.equals("BLACK", playerType)) {
            displayRow = 1 + boardRow;
        }

        out.print(displayRow + " ");

        ChessPosition start = null;
        HashSet<ChessPosition> endSet = new HashSet<>();

        if (highlights != null) {
            for (ChessMove move : highlights) {
                start = move.getStartPosition();
                ChessPosition end = move.getEndPosition();
                endSet.add(end);
            }
        }



        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; boardCol++) {
            ChessPosition check;
            if (playerType.equals("BLACK")) {
                check = new ChessPosition(boardRow + 1, BOARD_SIZE_IN_SQUARES - boardCol);
            } else {
                check = new ChessPosition(BOARD_SIZE_IN_SQUARES - boardRow, boardCol + 1);
            }

            if ((boardRow + boardCol) % 2 == 0){
                if (check.equals(start)){
                    out.print(SET_BG_COLOR_YELLOW);
                }
                else if (endSet.contains(check)){
                    out.print(SET_BG_COLOR_GREEN);
                }
                else{
                    out.print(SET_BG_COLOR_LIGHT_GREY);
                }
            } else {
                if (check.equals(start)){
                    out.print(SET_BG_COLOR_YELLOW);
                }
                else if (endSet.contains(check)) {
                    out.print(SET_BG_COLOR_DARK_GREEN);
                } else {
                    out.print(SET_BG_COLOR_DARK_GREY);
                }
            }

            ChessPiece piece;
            if (Objects.equals("BLACK", playerType)) {
                piece = game.getBoard().getPiece(new ChessPosition(boardRow + 1, BOARD_SIZE_IN_SQUARES - boardCol));
            }
            else{
                piece = game.getBoard().getPiece(new ChessPosition(BOARD_SIZE_IN_SQUARES - boardRow, boardCol + 1));
            }
            if (piece == null){
                out.print(EMPTY);
            }
            else{
                printPiece(out, piece, check, start);
            }
        }

        out.print(RESET_BG_COLOR);
        out.print(" " + displayRow);
        out.println();
    }

    private void printPiece(PrintStream out, ChessPiece piece, ChessPosition check, ChessPosition start) {
        ChessGame.TeamColor color = piece.getTeamColor();
        if (Objects.equals(ChessGame.TeamColor.BLACK, color)){
            if (check.equals(start)){
                printBlackHighlight(out, piece.getPieceType());
            }
            else{
                printBlack(out, piece.getPieceType());
            }
        }
        else{
            if (check.equals(start)){
                printWhiteHighlight(out, piece.getPieceType());
            }
            else{
                printWhite(out, piece.getPieceType());
            }
        }
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

    private void printBlackHighlight(PrintStream out, PieceType type) {
        switch (type){
            case PieceType.PAWN -> {
                out.print(SET_TEXT_COLOR_BLACK);
                out.print(BLACK_PAWN);
                out.print(RESET_TEXT_COLOR);
            }
            case PieceType.ROOK -> {
                out.print(SET_TEXT_COLOR_BLACK);
                out.print(BLACK_ROOK);
                out.print(RESET_TEXT_COLOR);
            }
            case PieceType.BISHOP -> {
                out.print(SET_TEXT_COLOR_BLACK);
                out.print(BLACK_BISHOP);
                out.print(RESET_TEXT_COLOR);
            }
            case PieceType.KNIGHT -> {
                out.print(SET_TEXT_COLOR_BLACK);
                out.print(BLACK_KNIGHT);
                out.print(RESET_TEXT_COLOR);
            }
            case PieceType.QUEEN -> {
                out.print(SET_TEXT_COLOR_BLACK);
                out.print(BLACK_QUEEN);
                out.print(RESET_TEXT_COLOR);
            }
            case PieceType.KING -> {
                out.print(SET_TEXT_COLOR_BLACK);
                out.print(BLACK_KING);
                out.print(RESET_TEXT_COLOR);
            }
        }
    }

    private void printWhiteHighlight(PrintStream out, PieceType type) {
        switch (type){
            case PieceType.PAWN -> {
                out.print(SET_TEXT_COLOR_BLACK);
                out.print(WHITE_PAWN);
                out.print(RESET_TEXT_COLOR);
            }
            case PieceType.ROOK -> {
                out.print(SET_TEXT_COLOR_BLACK);
                out.print(WHITE_ROOK);
                out.print(RESET_TEXT_COLOR);
            }
            case PieceType.BISHOP -> {
                out.print(SET_TEXT_COLOR_BLACK);
                out.print(WHITE_BISHOP);
                out.print(RESET_TEXT_COLOR);
            }
            case PieceType.KNIGHT -> {
                out.print(SET_TEXT_COLOR_BLACK);
                out.print(WHITE_KNIGHT);
                out.print(RESET_TEXT_COLOR);
            }
            case PieceType.QUEEN -> {
                out.print(SET_TEXT_COLOR_BLACK);
                out.print(WHITE_QUEEN);
                out.print(RESET_TEXT_COLOR);
            }
            case PieceType.KING -> {
                out.print(SET_TEXT_COLOR_BLACK);
                out.print(WHITE_KING);
                out.print(RESET_TEXT_COLOR);
            }
        }
    }

    public void printBoard(PrintStream out, Collection<ChessMove> highlights){
        drawHeaders(out);
        out.println();
        drawBoard(out, highlights);
        drawHeaders(out);
    }
}


