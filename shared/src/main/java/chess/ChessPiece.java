package chess;

import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
        boolean hasMoved = false;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */

    private static class Rule{
        boolean moveOnce;
        List<ChessPosition> directions;
        Rule(boolean moveOnce, List<ChessPosition> directions){
            this.directions = directions;
            this.moveOnce = moveOnce;

        }
        static Collection<ChessMove> movesFromPiece(ChessPiece piece, ChessPosition myPosition){
            Rule typerule = RuleLibrary.getRule(piece);
            HashSet<ChessMove> moves = new HashSet<>();
            for (ChessPosition direction : typerule.directions){

                ChessPosition nextPos = ChessPosition.add(myPosition, direction);
                int newRow = nextPos.getRow();
                int newCol = nextPos.getColumn();
                if (0 < newRow && newRow < 8){
                    if (0 < newCol && newCol < 8){
                        ChessMove newMove = new ChessMove(myPosition, nextPos, null);
                        moves.add(newMove);
                    }
                }

                }
            return moves;
            }





    };

    private static class RuleLibrary {

        static Rule getRule(ChessPiece piece) {
            PieceType type = piece.getPieceType();
            return rules(type);
        }

        private static Rule rules(PieceType type) {

            return switch (type) {
                case BISHOP -> new Rule(false, Arrays.asList(
                        new ChessPosition(1, 1),
                        new ChessPosition(1, -1),
                        new ChessPosition(-1, -1),
                        new ChessPosition(-1, 1)));
                case ROOK -> new Rule(false, Arrays.asList(
                        new ChessPosition(1, 0),
                        new ChessPosition(0, -1),
                        new ChessPosition(-1, 0),
                        new ChessPosition(0, 1)));
                case QUEEN -> new Rule(false, Arrays.asList(
                        new ChessPosition(1, 1),
                        new ChessPosition(1, -1),
                        new ChessPosition(-1, -1),
                        new ChessPosition(-1, 1),
                        new ChessPosition(1, 0),
                        new ChessPosition(0, -1),
                        new ChessPosition(-1, 0),
                        new ChessPosition(0, 1)));
                case KING -> new Rule(true, Arrays.asList(
                        new ChessPosition(1, 1),
                        new ChessPosition(1, -1),
                        new ChessPosition(-1, -1),
                        new ChessPosition(-1, 1),
                        new ChessPosition(1, 0),
                        new ChessPosition(0, -1),
                        new ChessPosition(-1, 0),
                        new ChessPosition(0, 1)));
                case KNIGHT -> new Rule(true, Arrays.asList(
                        new ChessPosition(1, -2),
                        new ChessPosition(2, -1),
                        new ChessPosition(2, 1),
                        new ChessPosition(1, 2),
                        new ChessPosition(-1, 2),
                        new ChessPosition(-2, 1),
                        new ChessPosition(-2, -1),
                        new ChessPosition(-1, -2)));
                case PAWN -> new Rule(true, Arrays.asList(
                        new ChessPosition(0, 1),
                        new ChessPosition(0, 2)));
                default -> null;
            };
        }
        }


        public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
            Collection<ChessMove> moves;
            ChessPiece piece = board.getPiece(myPosition);
            moves = Rule.movesFromPiece(piece, myPosition);

            /**
             * Implement Bishop and Rook first
             * Queen can use both Bishop and Rook (thats pretty much it for Queen)
             * Knight
             * King
             * Pawn (pawn is hardest)
             * implement in ChessPosition?
             * i am losing my mind here I feel like this shouldn't be so hard for me :(
             */


            return moves;
        }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }


}

