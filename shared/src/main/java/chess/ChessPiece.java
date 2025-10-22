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
    private boolean hasMoved;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
        this.hasMoved = false;
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
     * return Collection of valid moves
     */

    private static class Rule{
        boolean moveOnce;
        List<ChessPosition> directions;
        Rule(boolean moveOnce, List<ChessPosition> directions){
            this.directions = directions;
            this.moveOnce = moveOnce;

        }
        static HashSet<ChessMove> pawnPromotionMoves(ChessPosition myPosition, ChessPosition nextPos, HashSet<ChessMove> moves){
            ChessMove bMove = new ChessMove(myPosition, nextPos, PieceType.BISHOP);
            moves.add(bMove);
            ChessMove rMove = new ChessMove(myPosition, nextPos, PieceType.ROOK);
            moves.add(rMove);
            ChessMove kMove = new ChessMove(myPosition, nextPos, PieceType.KNIGHT);
            moves.add(kMove);
            ChessMove qMove = new ChessMove(myPosition, nextPos, PieceType.QUEEN);
            moves.add(qMove);

            return moves;
        }

        static Collection<ChessMove> movesFromPiece(ChessPiece piece, ChessPosition myPosition, ChessBoard board) {
            Rule typerule = RuleLibrary.getRule(piece);
            HashSet<ChessMove> moves = new HashSet<>();

            if (piece.getPieceType() != PieceType.PAWN) {
                for (ChessPosition direction : typerule.directions) {
                    ChessPosition newPos = myPosition;
                    boolean keepChecking = true;
                    while (keepChecking) {

                        ChessPosition nextPos = ChessPosition.add(newPos, direction);

                        int newRow = nextPos.getRow();
                        int newCol = nextPos.getColumn();

                        if (0 < newRow && newRow < 9 && 0 < newCol && newCol < 9) {

                            ChessPiece checkPiece = board.getPiece(nextPos);

                            if (checkPiece != null) {

                                if (checkPiece.getTeamColor() != piece.getTeamColor()) {

                                    ChessMove newMove = new ChessMove(myPosition, nextPos, null);
                                    moves.add(newMove);



                                }
                                keepChecking = false;
                            } else {
                                ChessMove newMove = new ChessMove(myPosition, nextPos, null);
                                moves.add(newMove);
                                newPos = nextPos;
                                if(typerule.moveOnce){
                                    keepChecking = false;
                                }
                            }


                        } else {
                            keepChecking = false;
                        }
                    }
                }
            }
            else{
                boolean blocked = false;
                for (ChessPosition direction : typerule.directions) {
                    ChessPosition nextPos = ChessPosition.add(myPosition, direction);

                    int newRow = nextPos.getRow();
                    int newCol = nextPos.getColumn();

                    if (0 < newRow && newRow < 9 && 0 < newCol && newCol < 9) {

                        ChessPiece checkPiece = board.getPiece(nextPos);

                        if (checkPiece != null) {

                            if (direction.getColumn() != 0){
                                if (checkPiece.getTeamColor() != piece.getTeamColor()){
                                    if (newRow == 8 || newRow == 1){
                                        moves = pawnPromotionMoves(myPosition, nextPos, moves);}
                                    else{ChessMove newMove = new ChessMove(myPosition, nextPos, null);
                                        moves.add(newMove);}
                                }

                            }
                            else {blocked = true;}
                        } else {
                            if (!blocked && direction.getColumn()== 0) {
                                if (newRow == 8 || newRow == 1){
                                    moves = pawnPromotionMoves(myPosition, nextPos, moves);
                                }
                                else{
                                    ChessMove newMove = new ChessMove(myPosition, nextPos, null);
                                    moves.add(newMove);}

                            }
                        }


                    }
                }

            }
            return moves;
        }

    }
    private static class RuleLibrary {

        static Rule getRule(ChessPiece piece) {
            PieceType type = piece.getPieceType();
            return rules(type, piece.pieceColor, piece.hasMoved);
        }

        private static Rule rules(PieceType type, ChessGame.TeamColor color, boolean hasMoved) {

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
                case PAWN -> switch(color) {
                    case WHITE -> {
                        if (hasMoved) {
                            yield new Rule(true, Arrays.asList(
                                    new ChessPosition(1, 0),
                                    new ChessPosition(1, 1),
                                    new ChessPosition(1, -1)
                            ));
                        } else {
                            yield new Rule(true, Arrays.asList(
                                    new ChessPosition(1, 0),
                                    new ChessPosition(2, 0),
                                    new ChessPosition(1, 1),
                                    new ChessPosition(1, -1)
                            ));
                        }
                    }
                    case BLACK -> {
                        if (hasMoved) {
                            yield new Rule(true, Arrays.asList(
                                    new ChessPosition(-1, 0),
                                    new ChessPosition(-1, 1),
                                    new ChessPosition(-1, -1)
                            ));
                        } else {
                            yield new Rule(true, Arrays.asList(
                                    new ChessPosition(-1, 0),
                                    new ChessPosition(-2, 0),
                                    new ChessPosition(-1, 1),
                                    new ChessPosition(-1, -1)
                            ));
                        }
                    }
                };

            };
        }
    }


    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves;
        ChessPiece piece = board.getPiece(myPosition);
        if(piece.getPieceType() == PieceType.PAWN && piece.pieceColor== ChessGame.TeamColor.WHITE && myPosition.getRow()!=2){
            hasMoved = true;
        }
        if(piece.getPieceType() == PieceType.PAWN && piece.pieceColor== ChessGame.TeamColor.BLACK && myPosition.getRow()!=7){
            hasMoved = true;
        }
        moves = Rule.movesFromPiece(piece, myPosition, board);


        return moves;
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
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