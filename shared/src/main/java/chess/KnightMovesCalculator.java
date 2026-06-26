package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        moves.addAll(PieceMovesCalculator.computeDir(2,1, false, board, myPosition));
        moves.addAll(PieceMovesCalculator.computeDir(2,-1, false, board, myPosition));
        moves.addAll(PieceMovesCalculator.computeDir(-2,-1, false, board, myPosition));
        moves.addAll(PieceMovesCalculator.computeDir(-2,1, false, board, myPosition));
        moves.addAll(PieceMovesCalculator.computeDir(1,2, false, board, myPosition));
        moves.addAll(PieceMovesCalculator.computeDir(1,-2, false, board, myPosition));
        moves.addAll(PieceMovesCalculator.computeDir(-1,-2, false, board, myPosition));
        moves.addAll(PieceMovesCalculator.computeDir(-1,2, false, board, myPosition));
        return moves;
    }
}
