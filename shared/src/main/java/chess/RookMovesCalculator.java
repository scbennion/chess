package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator implements PieceMovesCalculator{

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        moves.addAll(PieceMovesCalculator.computeDir(0,1, true, board, myPosition));
        moves.addAll(PieceMovesCalculator.computeDir(0,-1, true, board, myPosition));
        moves.addAll(PieceMovesCalculator.computeDir(-1,0, true, board, myPosition));
        moves.addAll(PieceMovesCalculator.computeDir(1,0, true, board, myPosition));
        return moves;
    }
}
