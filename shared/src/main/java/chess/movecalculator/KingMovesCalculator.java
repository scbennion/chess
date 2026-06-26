package chess.movecalculator;

import chess.ChessBoard;
import chess.ChessMove;
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
            return moves;

        }
    }

