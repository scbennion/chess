package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator implements PieceMovesCalculator{

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int col = myPosition.getColumn();
        int row = myPosition.getRow();

        for (int c = 1; c <= board.getSize(); c++) {
            int northEastRow = row + (c - col); //NE
            if (northEastRow <= board.getSize() && northEastRow >= 1) {
                if (board.getPiece(new ChessPosition(northEastRow, c)) == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(northEastRow, c), null));
                }
            }

            int southEastRow = row - (c - col); //SE
            if (southEastRow >= 1 && southEastRow <= board.getSize()) {
                if (board.getPiece(new ChessPosition(southEastRow, c)) == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(southEastRow, c), null));
                }
            }
        }
        return moves;
    }
}
