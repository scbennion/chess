package chess;

import java.util.*;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor turn;
    private final Set<ChessMove> previousEnPassantOpportunities;
    private final Set<ChessMove> enPassantOpportunities;


    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        turn = TeamColor.WHITE;
        previousEnPassantOpportunities = new HashSet<>();
        enPassantOpportunities = new HashSet<>();
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
        ChessPiece p = board.getPiece(startPosition);
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        if (p != null) {
            TeamColor c = p.getTeamColor();
            for (ChessMove m : p.pieceMoves(board, startPosition)) {
                if (isMoveWithoutCheck(m, c)) {
                    addMoveAndCheckForSpecialMoves(m, c, validMoves);
                }
            }
        }
        return validMoves;
    }

    private void addMoveAndCheckForSpecialMoves(ChessMove m, TeamColor c, ArrayList<ChessMove> validMoves) {
        switch (m.getSpecialMove()) {
            case LEFT_CASTLE -> {
                if (isMoveWithoutCheck(new ChessMove(m.getStartPosition(), m.getStartPosition(), null), c)
                        && isMoveWithoutCheck(new ChessMove(m.getStartPosition(), new ChessPosition(m.getStartPosition().getRow(), 2), null), c)
                        && isMoveWithoutCheck(new ChessMove(m.getStartPosition(), new ChessPosition(m.getStartPosition().getRow(), 3), null), c)
                        && isMoveWithoutCheck(new ChessMove(m.getStartPosition(), new ChessPosition(m.getStartPosition().getRow(), 4), null), c)) {
                    validMoves.add(m);
                }
            }
            case RIGHT_CASTLE -> {
                if (isMoveWithoutCheck(new ChessMove(m.getStartPosition(), m.getStartPosition(), null), c)
                        && isMoveWithoutCheck(new ChessMove(m.getStartPosition(), new ChessPosition(m.getStartPosition().getRow(), 6), null), c)
                        && isMoveWithoutCheck(new ChessMove(m.getStartPosition(), new ChessPosition(m.getStartPosition().getRow(), 7), null), c)) {
                    validMoves.add(m);
                }
            }
            case LEFT_EN_PASSANT -> {
                ChessBoard testCheckBoard = new ChessBoard(board);
                testCheckBoard.removePiece(new ChessPosition(m.getStartPosition().getRow(), m.getStartPosition().getColumn() - 1));
                if (isMoveWithoutCheck(m, c, testCheckBoard) && !previousEnPassantOpportunities.contains(m)) {
                    validMoves.add(m);
                    enPassantOpportunities.add(m);
                }

            }
            case RIGHT_EN_PASSANT -> {
                ChessBoard testCheckBoard = new ChessBoard(board);
                testCheckBoard.removePiece(new ChessPosition(m.getStartPosition().getRow(), m.getStartPosition().getColumn() + 1));
                if (isMoveWithoutCheck(m, c, testCheckBoard) && !previousEnPassantOpportunities.contains(m)) {
                    validMoves.add(m);
                    enPassantOpportunities.add(m);
                }
            }
            case null -> validMoves.add(m);
        }
    }

    private boolean isMoveWithoutCheck(ChessMove m, TeamColor c) {
        return isMoveWithoutCheck(m, c, new ChessBoard(board));
    }

    private boolean isMoveWithoutCheck(ChessMove m, TeamColor c, ChessBoard testCheckBoard) {
        testCheckBoard.movePiece(m);
        return !boardInCheck(c, testCheckBoard);
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
        if (piece == null || piece.getTeamColor() != turn) {
            throw new InvalidMoveException();
        }
        if (move.getSpecialMove() == null) {
            move.trySetSpecialMoveField(board);
        }

        //to update en-passant stuff. SUPER SCUFFED
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                validMoves(new ChessPosition(row, col));
            }
        }

        Collection<ChessMove> moves = validMoves(startPos);
        if (moves.contains(move)) {
            board.movePiece(move);
            tryMoveSecondPiece(move);
            turn = oppositeColor(turn);
            previousEnPassantOpportunities.addAll(enPassantOpportunities);
            enPassantOpportunities.clear();
        } else {
            throw new InvalidMoveException();
        }
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
                }
                case RIGHT_CASTLE -> {
                    int row = m.getStartPosition().getRow();
                    board.movePiece(new ChessMove(new ChessPosition(row, 8), new ChessPosition(row, 6), null));
                }
                case LEFT_EN_PASSANT ->
                        board.removePiece(new ChessPosition(m.getStartPosition().getRow(), m.getStartPosition().getColumn() - 1));
                case RIGHT_EN_PASSANT ->
                        board.removePiece(new ChessPosition(m.getStartPosition().getRow(), m.getStartPosition().getColumn() + 1));

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
        }
        return false;
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
        for (ChessPosition p : board.getSidePieces(teamColor).keySet()) {
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
        for (ChessPosition p : board.getSidePieces(teamColor).keySet()) {
            moves.addAll(validMoves(p));
        }
        if (!isInCheck(teamColor)) {
            return moves.isEmpty();
        }
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
        return board.toString() + turn.toString() + "\n";
    }
}
