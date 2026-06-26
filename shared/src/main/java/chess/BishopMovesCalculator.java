package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator implements PieceMovesCalculator{

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        moves.addAll(computeDir(1,1, board, myPosition));
        moves.addAll(computeDir(1,-1, board, myPosition));
        moves.addAll(computeDir(-1,-1, board, myPosition));
        moves.addAll(computeDir(-1,1, board, myPosition));
        return moves;
    }

    /*
    Check the valid moves for a piece in a given direction
     */
    private ArrayList<ChessMove> computeDir(int rowDir, int colDir, ChessBoard board, ChessPosition pos) {
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
        }
        return moves;


    }
}
