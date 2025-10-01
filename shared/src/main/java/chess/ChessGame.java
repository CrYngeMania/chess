package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;


/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard gameboard = new ChessBoard();
    private TeamColor teamTurn;
    public ChessGame() {
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessBoard board = getBoard();
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null){
            return null;
        }
        TeamColor color = piece.getTeamColor();
        ChessPosition teamKing = gameboard.getKing(color);

        Collection<ChessMove> allPossible = piece.pieceMoves(board, startPosition);
        for(ChessMove move: allPossible){
            ChessBoard copyBoard = gameboard;

        }

    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = gameboard.getPiece(move.getStartPosition());
        int newRow = move.getEndPosition().getRow();
        int newCol = move.getEndPosition().getColumn();
        ChessPosition newPos = new ChessPosition(newRow, newCol);
        gameboard.addPiece(newPos, piece);
        gameboard.addPiece(move.getStartPosition(), null);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition checkKing = gameboard.getKing(teamColor);
        Collection<ChessPosition> otherPieces;
        if (teamColor == TeamColor.WHITE) {
            otherPieces = getBoard().getTeamPieces(TeamColor.BLACK);
        } else {
            otherPieces = getBoard().getTeamPieces(TeamColor.WHITE);
        }
        for (ChessPosition position : otherPieces) {
            ChessPiece piece = gameboard.getPiece(position);
            Collection<ChessMove> allPossible = piece.pieceMoves(gameboard, position);
            for (ChessMove move : allPossible) {
                if (move.getEndPosition() == checkKing) {
                    return true;
                }
            }
        }
    return false;

        /**
         * how do i know when a king is in check?
         * there is a piece(s) attacking it
         * how do I know if a piece is attacking?
         * check every enemy piece and see if the king's current position is in their possible moves list
         */
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameboard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameboard;
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "gameboard=" + gameboard +
                ", teamTurn=" + teamTurn +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(gameboard, chessGame.gameboard) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameboard, teamTurn);
    }
}
