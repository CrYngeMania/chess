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
    private ChessMove lastMove;
    public ChessGame() {
        gameboard.resetBoard();
        teamTurn = TeamColor.WHITE;
        lastMove = null;
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
    public Collection<ChessMove> validMoves(ChessPosition startPosition){

        HashSet<ChessMove> valid = new HashSet<>();
        ChessBoard board = getBoard();
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null){
            return null;
        }
        TeamColor color = piece.getTeamColor();
        ChessPosition teamKing = gameboard.getKing(color);

        Collection<ChessMove> allPossible = piece.pieceMoves(board, startPosition);
        for(ChessMove move: allPossible){
            ChessBoard original = gameboard;

                gameboard = original.copy();

                gameboard.addPiece(move.getEndPosition(), piece);
                gameboard.addPiece(startPosition, null);
                if (!isInCheck(color)) {
                    valid.add(move);
                }

                gameboard = original;

        }

        if (lastMove == null){return valid;}

        ChessPiece lastPiece = board.getPiece(lastMove.getEndPosition());

        if (lastPiece == null || lastPiece.getPieceType() != ChessPiece.PieceType.PAWN) {
            return valid;
        }
        int startRow = lastMove.getStartPosition().getRow();
        int endRow = lastMove.getEndPosition().getRow();
        if (startRow - endRow == 2 || startRow - endRow == -2){
            ChessPosition checkRight = new ChessPosition(startPosition.getRow(), startPosition.getColumn()+1);
            ChessPosition checkLeft = new ChessPosition(startPosition.getRow(), startPosition.getColumn()-1);

            int rowDir = (piece.getTeamColor()== TeamColor.WHITE) ? 1 : -1;
            if (checkRight.equals(lastMove.getEndPosition())){
                ChessPosition ePPos = new ChessPosition(startPosition.getRow()+ rowDir, startPosition.getColumn()+1);
                ChessMove ePRight = new ChessMove(startPosition, ePPos, null);
                valid.add(ePRight);

            }
            else if (checkLeft.equals(lastMove.getEndPosition())){

                ChessPosition ePPos = new ChessPosition(startPosition.getRow()+rowDir, startPosition.getColumn()-1);
                ChessMove ePLeft = new ChessMove(startPosition, ePPos, null);
                valid.add(ePLeft);
            }
        }


        return valid;

    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = gameboard.getPiece(move.getStartPosition());
        if (piece == null) {
            throw new InvalidMoveException("No piece at start position.");
        }
        if (piece.getTeamColor()!= teamTurn) {
            throw new InvalidMoveException("It's not your turn bucko >:(");
        }
        Collection<ChessMove> valid = validMoves(move.getStartPosition());
        if (valid.contains(move)) {
            int newRow = move.getEndPosition().getRow();
            int newCol = move.getEndPosition().getColumn();
            ChessPosition newPos = new ChessPosition(newRow, newCol);
            if (piece.getPieceType() == ChessPiece.PieceType.PAWN && move.getPromotionPiece()!= null){
                ChessPiece promotion = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
                gameboard.addPiece(newPos, promotion);
                gameboard.addPiece(move.getStartPosition(), null);
            }
            else{
                gameboard.addPiece(newPos, piece);
                gameboard.addPiece(move.getStartPosition(), null);
            }
            if (lastMove != null) {
                ChessPiece lastPiece = gameboard.getPiece(lastMove.getEndPosition());
                if (lastPiece.getPieceType() == ChessPiece.PieceType.PAWN) {
                    if (lastMove.getEndPosition().getColumn() == newCol){
                        gameboard.addPiece(lastMove.getEndPosition(), null);
                    }
                }
            }

        }
        else{
            throw new InvalidMoveException("Not a valid move.");
        }
        if (piece.getTeamColor() == TeamColor.BLACK){
            setTeamTurn(TeamColor.WHITE);
        }
        else if (piece.getTeamColor() == TeamColor.WHITE){
            setTeamTurn(TeamColor.BLACK);
        }
        lastMove = move;
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
                int newRow = move.getEndPosition().getRow();
                int newCol = move.getEndPosition().getColumn();
                if (newRow == checkKing.getRow() && newCol == checkKing.getColumn()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor){
        if (isInCheck(teamColor)){
            Collection<ChessPosition> checkPieces;
            if (teamColor == TeamColor.WHITE) {
                checkPieces = getBoard().getTeamPieces(TeamColor.WHITE);
            } else {
                checkPieces = getBoard().getTeamPieces(TeamColor.BLACK);
            }
            for (ChessPosition position : checkPieces) {

                Collection<ChessMove> valid = validMoves(position);
                if (!valid.isEmpty()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(!isInCheck(teamColor)){
            Collection<ChessPosition> checkPieces;
            if (teamColor == TeamColor.WHITE) {
                checkPieces = getBoard().getTeamPieces(TeamColor.WHITE);
            } else {
                checkPieces = getBoard().getTeamPieces(TeamColor.BLACK);
            }
            for (ChessPosition position : checkPieces) {

                Collection<ChessMove> valid = validMoves(position);
                if (!valid.isEmpty()) {
                    return false;
                }
            }
            return true;
        }
        return false;

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