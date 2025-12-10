package client;

import chess.*;
import client.websocket.ServerMessageHandler;
import com.google.gson.Gson;
import exception.ResponseException;
import facade.ServerFacade;
import client.websocket.WebSocketFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import websocket.messages.ErrorMessage;
import websocket.messages.GameNotificationMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

import java.io.PrintStream;
import java.util.*;

import static chess.ChessPiece.*;
import static ui.EscapeSequences.*;

public class GameClient implements ServerMessageHandler{
    private static final Logger log = LoggerFactory.getLogger(GameClient.class);
    private final WebSocketFacade ws;
    String playerType;
    ChessGame game;
    PrintStream out = new PrintStream(System.out, true);
    Boolean gameComplete = false;
    String authToken;
    Integer gameID;

    @Override
    public void notify(String message) {
        Gson serialiser = new Gson();
        ServerMessage msg = serialiser.fromJson(message, ServerMessage.class);
        switch (msg.getServerMessageType()) {
            case LOAD_GAME -> {
                LoadGameMessage lgm = serialiser.fromJson(message, LoadGameMessage.class);
                game = lgm.getGame();
                System.out.print("\n");
                printBoard(out, null);
                printPrompt();
            }
            case NOTIFICATION -> {
                GameNotificationMessage gnm = serialiser.fromJson(message, GameNotificationMessage.class);
                System.out.println("Notification: " + gnm.getMessage());
                printPrompt();
            }
            case ERROR -> {
                ErrorMessage em = serialiser.fromJson(message, ErrorMessage.class);
                System.out.println(em.getErrorMessage());
                printPrompt();
            }
        }
    }

    public GameClient(ServerFacade server, String playerType, ChessGame game, Integer gameID, String url) {
        try{
            this.ws = new WebSocketFacade(url, this);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        this.playerType = playerType;
        this.game = game;
        this.authToken = server.getAuth();
        this.gameID = gameID;
    }

    /// Running implementation
    public void run() throws ResponseException {
        try{
            ws.connect(authToken, gameID);
        } catch (ResponseException e) {
            throw new RuntimeException(e.getMessage());
        }
        Scanner scanner = new Scanner(System.in);
        var output = "";
        do {
            printPrompt();
            String inputLine = scanner.nextLine();
            try {
                output = evaluate(inputLine);
                System.out.print(output);
            } catch (Throwable e) {
                log.error(String.valueOf(e));
            }
        } while (!output.equals("Leaving!"));
        System.out.println();
        ws.leaveGame(authToken, gameID);
    }

    public String evaluate(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "help" -> help();
                case "redraw" -> reprintBoard(out);
                case "leave" -> "Leaving!";
                case "move" -> makeMove(params);
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

    public String resign() throws ResponseException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Are you sure you want to resign? y/n\n");
        String answer = scanner.nextLine();
        if (Objects.equals(answer, "y")){
            gameComplete = true;
            ws.resign(authToken, gameID);
            return "Better luck next time!";
        }
        else {
            return "Good luck!";
        }
    }

    public String makeMove(String... params) throws ResponseException, InvalidMoveException {

        if (params[0] == null || params[0].length() < 2) {
            return "Hey, that's not valid, pal...";
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
        ChessPiece piece = game.getBoard().getPiece(start);
        if (piece.getPieceType() == PieceType.PAWN && (endCol == 8 || endCol == 1)){
            Scanner scanner = new Scanner(System.in);
            System.out.print("Promote to: (first letter) ");
            String answer = scanner.nextLine();
            switch (answer.toLowerCase()){
                case "b" ->

                    move = new ChessMove(start, end, PieceType.BISHOP);

                case "r" ->

                    move = new ChessMove(start, end, PieceType.ROOK);

                case "k" ->

                    move = new ChessMove(start, end, PieceType.KNIGHT);

                case "q" ->

                    move = new ChessMove(start, end, PieceType.QUEEN);

            }
        }

        ws.makeMove(authToken, gameID, move);

        if (game.isInCheckmate(ChessGame.TeamColor.BLACK) || game.isInCheckmate(ChessGame.TeamColor.WHITE)){
            gameComplete = true;
        }
        return "";
    }

    private void printPrompt() {
        System.out.print("\n" + "GAME " + ">>> ");}

    /// Draw Board functions
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

        int displayRow = BOARD_SIZE_IN_SQUARES - boardRow;
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
        boolean isWhite = piece.getTeamColor() == ChessGame.TeamColor.WHITE;
        boolean highlighted = check.equals(start);

        printPieceSymbol(out, piece.getPieceType(), isWhite, highlighted);
    }

    private void printPieceSymbol(PrintStream out, PieceType type, boolean isWhite, boolean highlighted) {

        String colorCode = highlighted ? SET_TEXT_COLOR_BLACK : (isWhite ? SET_TEXT_COLOR_WHITE : SET_TEXT_COLOR_RED);

        String symbol;
        switch (type) {
            case PAWN -> symbol = isWhite ? WHITE_PAWN : BLACK_PAWN;
            case ROOK -> symbol = isWhite ? WHITE_ROOK : BLACK_ROOK;
            case BISHOP -> symbol = isWhite ? WHITE_BISHOP : BLACK_BISHOP;
            case KNIGHT -> symbol = isWhite ? WHITE_KNIGHT : BLACK_KNIGHT;
            case QUEEN -> symbol = isWhite ? WHITE_QUEEN : BLACK_QUEEN;
            case KING -> symbol = isWhite ? WHITE_KING : BLACK_KING;
            default -> throw new IllegalArgumentException("Unknown piece type: " + type);
        }

        out.print(colorCode);
        out.print(symbol);
        out.print(RESET_TEXT_COLOR);
    }


    public void printBoard(PrintStream out, Collection<ChessMove> highlights){
        drawHeaders(out);
        out.println();
        drawBoard(out, highlights);
        drawHeaders(out);
    }
}


