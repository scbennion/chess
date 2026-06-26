package chess.movecalculator;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public interface PieceMovesCalculator {
    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);
    /*
    Check the valid moves for a piece in a given direction
     */
    static ArrayList<ChessMove> computeDir(int rowDir, int colDir, boolean longRange, ChessBoard board, ChessPosition pos) {
        int size = board.getSize();
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor color = board.getPiece(pos).getTeamColor();
        int col = pos.getColumn();
        int row = pos.getRow();
        while (col + colDir >= 1 && col + colDir <= size && row + rowDir >= 1 && row + rowDir <= size) {
            col += colDir;
            row += rowDir;
            ChessPosition newPos = new ChessPosition(row, col);
            ChessPiece p = board.getPiece(newPos);
            ChessMove m = new ChessMove(pos, newPos, null);

            if (p == null) { //empty square
                moves.add(m);
            } else if (p.getTeamColor() == color) { //blocked by my piece
                break;
            } else { //blocked by enemy piece
                moves.add(m);
                break;
            }
            if (!longRange) {
                break;
            }
        }
        return moves;
    }
}
