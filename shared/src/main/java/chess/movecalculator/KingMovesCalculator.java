package chess.movecalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator implements PieceMovesCalculator {

        @Override
        public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
            Collection<ChessMove> moves = new ArrayList<>();
            for (int rowDir = -1; rowDir <= 1; rowDir++) {
                for (int colDir = -1; colDir <= 1; colDir++) {
                    if (rowDir != 0 | colDir != 0) {
                        moves.addAll(PieceMovesCalculator.computeDir(rowDir, colDir, false, board, myPosition));
                    }
                }
            }
            moves.addAll(castleMoves(board, myPosition));
            return moves;

        }

    /**
     * Attempts to add castling to the possible moves for the king
     * Checks for whether the rook and king have been moved, and whether there is open space between them
     *
     */
        private Collection<ChessMove> castleMoves(ChessBoard board, ChessPosition myPos) {
            Collection<ChessMove> moves = new ArrayList<>();
            int row = myPos.getRow();
            ChessPiece piece = board.getPiece(myPos);
            if (piece.getMoveCount() == 0) {
                ChessPiece left_rook = board.getPiece(new ChessPosition(row, 1));
                if (left_rook != null
                        && left_rook.getPieceType() == ChessPiece.PieceType.ROOK
                        && left_rook.getMoveCount() == 0
                        && board.getPiece(new ChessPosition(row, 2)) == null
                        && board.getPiece(new ChessPosition(row, 3)) == null
                        && board.getPiece(new ChessPosition(row, 4)) == null) {
                    moves.add(new ChessMove(myPos, new ChessPosition(row, 3), null, ChessMove.SpecialMove.LEFT_CASTLE));
                }

                ChessPiece right_rook = board.getPiece(new ChessPosition(row, 8));
                if (right_rook != null
                        && right_rook.getPieceType() == ChessPiece.PieceType.ROOK
                        && right_rook.getMoveCount() == 0
                        && board.getPiece(new ChessPosition(row, 6)) == null
                        && board.getPiece(new ChessPosition(row, 7)) == null) {
                    moves.add(new ChessMove(myPos, new ChessPosition(row, 7), null, ChessMove.SpecialMove.RIGHT_CASTLE));
                }
            }
            return moves;
        }
    }

