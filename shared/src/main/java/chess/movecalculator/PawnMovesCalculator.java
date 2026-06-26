package chess.movecalculator;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator {
    boolean moved; //whether the pawn is in the initial position
    boolean promotion; //whether the pawn will promote
    int orientation; //1 is White, -1 is Black
    ChessGame.TeamColor color; //piece color

    public PawnMovesCalculator(ChessGame.TeamColor color, ChessPosition pos, int size) {
        this.color = color;
        int row = pos.getRow();
        orientation = color == ChessGame.TeamColor.WHITE ? 1 : -1;
        promotion = (row + orientation == 1 | row + orientation == size);
        moved = orientation > 0 ? row != 2 : row != size - 1 ;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition pos) {

        int size = board.getSize();
        ArrayList<ChessMove> moves = new ArrayList<>();
        int col = pos.getColumn();
        int row = pos.getRow();
        boolean rowInBounds = (row + orientation >= 1) && (row + orientation <= size);

        if (rowInBounds) {
            if (board.getPiece(new ChessPosition(row + orientation, col)) == null) { //Directly in front
                moves.addAll(movePossiblePromotion(pos, new ChessPosition (row + orientation, col)));
                if (!moved && board.getPiece(new ChessPosition(row + orientation * 2, col)) == null) { //Initial 2 square move
                    moves.add(new ChessMove(pos, new ChessPosition(row + orientation * 2, col), null));
                }
            } if (col + 1 <= size && board.getPiece(new ChessPosition(row + orientation, col + 1)) != null //NE capture
                    && board.getPiece(new ChessPosition(row + orientation, col + 1)).getTeamColor() != color) {
                moves.addAll(movePossiblePromotion(pos, new ChessPosition (row + orientation, col + 1)));
            } if (col - 1 >= 1 && board.getPiece(new ChessPosition(row + orientation, col - 1)) != null //SE capture
                    && board.getPiece(new ChessPosition(row + orientation, col - 1)).getTeamColor() != color) {
                moves.addAll(movePossiblePromotion(pos, new ChessPosition(row + orientation, col - 1)));
            }
        }
        return moves;
    }

    private ArrayList<ChessMove> movePossiblePromotion(ChessPosition startPos, ChessPosition endPos) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        if (promotion) {
            moves.add(new ChessMove(startPos, endPos, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(startPos, endPos, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(startPos, endPos, ChessPiece.PieceType.KNIGHT));
            moves.add(new ChessMove(startPos, endPos, ChessPiece.PieceType.BISHOP));
        } else {
            moves.add(new ChessMove(startPos, endPos, null));
        }
        return moves;
    }

}
