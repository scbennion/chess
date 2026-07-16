package chess;

import chess.movecalculator.PawnMovesCalculator;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;
    private SpecialMove specialMove;

    public enum SpecialMove {
        LEFT_CASTLE,
        RIGHT_CASTLE,
        LEFT_EN_PASSANT,
        RIGHT_EN_PASSANT
    }


    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
        this.specialMove = null;
    }

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece, SpecialMove specialMove) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
        this.specialMove = specialMove;
    }


    public SpecialMove getSpecialMove() {
        return specialMove;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

    public void trySetSpecialMoveField(ChessBoard board) {
        ChessPiece.PieceType pieceType = board.getPiece(startPosition).getPieceType();
        if (pieceType == ChessPiece.PieceType.KING) {
            int result = startPosition.getColumn() - endPosition.getColumn();
            if (result == 2) {
                specialMove = SpecialMove.LEFT_CASTLE;
            } else if (result == -2) {
                specialMove = SpecialMove.RIGHT_CASTLE;
            }
        } else if (pieceType == ChessPiece.PieceType.PAWN) {
            if (new PawnMovesCalculator(board.getPiece(startPosition).getTeamColor(), startPosition, board.size)
                    .isEnPassant(board, startPosition, -1)) {
                specialMove = SpecialMove.LEFT_EN_PASSANT;
            } else if (new PawnMovesCalculator(board.getPiece(startPosition).getTeamColor(), startPosition, board.size)
                    .isEnPassant(board, startPosition, 1)) {
                specialMove = SpecialMove.RIGHT_EN_PASSANT;
            }
        }
    }


    @Override
    public String toString() {
        if (promotionPiece == null) {
            return String.format("[%s, %s]", startPosition, endPosition);
        } else {
            return String.format("[%s, %s, %s, %s]", startPosition, endPosition, promotionPiece, specialMove);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(startPosition, chessMove.startPosition)
                && Objects.equals(endPosition, chessMove.endPosition)
                && promotionPiece == chessMove.promotionPiece;
                //&& specialMove == chessMove.specialMove; Can't do this because of how test cases are set up
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPosition, endPosition, promotionPiece); //specialMove;
    }
}
