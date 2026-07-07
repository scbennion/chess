package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    ChessBoard board;
    TeamColor turn;
    ChessPosition lastMovedPosition;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        turn = TeamColor.WHITE;
        lastMovedPosition = null;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }


    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        System.out.println(board);
        ChessPiece p =  board.getPiece(startPosition);
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        if (p != null) {
            TeamColor c = p.getTeamColor();
            for (ChessMove m : p.pieceMoves(board, startPosition)) {
                ChessBoard testCheckBoard = new ChessBoard(board);
                testCheckBoard.movePiece(m);
                if (!boardInCheck(c, testCheckBoard)) {
                    validMoves.add(m);
                }
            }
        }
        return validMoves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPos = move.getStartPosition();
        ChessPiece piece = board.getPiece(startPos);
        if (piece == null || piece.getTeamColor() != turn)
            throw new InvalidMoveException();

        if (move.getSpecialMove() == null) {
            move = trySetSpecialMoveField(move);
        }

        Collection<ChessMove> moves = validMoves(startPos);
        if (moves.contains(move)) {
            board.movePiece(move);
            tryMoveSecondPiece(move);
            turn = oppositeColor(turn);
            lastMovedPosition = move.getEndPosition();
        } else {
            throw new InvalidMoveException();
        }
    }

    /**
     * Some of the test cases in Castling Tests don't set the special move
     * flag as intended, so it has to be to set by deduction
     */
    private ChessMove trySetSpecialMoveField(ChessMove move) {
        if (board.getPiece(move.getStartPosition()).getPieceType() == ChessPiece.PieceType.KING) {
            int result = move.getStartPosition().getColumn() - move.getEndPosition().getColumn();
            if (result == 2) {
                move = new ChessMove(move.getStartPosition(), move.getEndPosition(), null, ChessMove.SpecialMove.LEFT_CASTLE);
            }
            else if (result == -2) {
                move = new ChessMove(move.getStartPosition(), move.getEndPosition(), null, ChessMove.SpecialMove.RIGHT_CASTLE);
            }
        }
        return move;
    }


    /**
     * Moves the second piece for special moves like castling and en passant
     */
    private void tryMoveSecondPiece(ChessMove m) {
        if (m.getSpecialMove() != null) {
            switch (m.getSpecialMove()) {
                case LEFT_CASTLE -> {
                    int row = m.getStartPosition().getRow();
                    board.movePiece(new ChessMove(new ChessPosition(row, 1), new ChessPosition(row, 4), null));
                } case RIGHT_CASTLE -> {
                    int row = m.getStartPosition().getRow();
                    board.movePiece(new ChessMove(new ChessPosition(row, 8), new ChessPosition(row, 6), null));
                } case LEFT_EN_PASSANT -> {
                } case RIGHT_EN_PASSANT -> {
                }
            }
        }
    }


    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return boardInCheck(teamColor, this.board);
    }

    private boolean boardInCheck(TeamColor teamColor, ChessBoard board) {
        ChessPosition kingPos = board.getKingPos(teamColor);
        Map<ChessPosition, ChessPiece> pieces = board.getSidePieces(oppositeColor(teamColor));
        for (ChessPosition pos : pieces.keySet()) {
            for (ChessMove m : pieces.get(pos).pieceMoves(board, pos)) {
                if (m.getEndPosition().equals(kingPos)) {
                    return true;
                }
            }
        } return false;
    }

    private TeamColor oppositeColor(TeamColor c) {
        return switch (c) {
            case WHITE -> TeamColor.BLACK;
            case BLACK -> TeamColor.WHITE;
        };
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        for (ChessPosition p: board.getSidePieces(teamColor).keySet()) {
            moves.addAll(validMoves(p));
        }
        return moves.isEmpty() && isInCheck(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        for (ChessPosition p: board.getSidePieces(teamColor).keySet()) {
            moves.addAll(validMoves(p));
        }
        if (!isInCheck(teamColor))
            return moves.isEmpty();
        return false;
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && turn == chessGame.turn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, turn);
    }

    @Override
    public String toString() {
        return  board.toString() + turn.toString() + "\n";
    }
}
