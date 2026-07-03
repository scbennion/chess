package chess;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    ChessPiece[][] squares;
    int size = 8;
    private HashMap<ChessGame.TeamColor, ChessPosition> kingPos;

    public ChessBoard() {
        squares = new ChessPiece[size][size];
        kingPos = new HashMap<>();
    }

    /**
     * Adds a chess piece to the chessboard
     * If a chess piece is a king, adds it to the kingPos hashmap
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece)  {
        squares[position.getRow()-1][position.getColumn()-1] = piece;
        ChessGame.TeamColor color = piece.getTeamColor();
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            kingPos.put(color, position);
        }
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()-1][position.getColumn()-1];
    }

    public ChessPosition getKingPos(ChessGame.TeamColor color) {
        return kingPos.get(color);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        squares = new ChessPiece[size][size];
        makeBackRow(1, ChessGame.TeamColor.WHITE);
        makeBackRow(8, ChessGame.TeamColor.BLACK);
        makePawnRow(2, ChessGame.TeamColor.WHITE);
        makePawnRow(7, ChessGame.TeamColor.BLACK);
    }

    /**
    Makes the back row and sets the King Position
     */
    private void makeBackRow(int row, ChessGame.TeamColor color) {
        squares[row-1][0] = new ChessPiece(color, ChessPiece.PieceType.ROOK);
        squares[row-1][1] = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
        squares[row-1][2] = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
        squares[row-1][3] = new ChessPiece(color, ChessPiece.PieceType.QUEEN);
        squares[row-1][4] = new ChessPiece(color, ChessPiece.PieceType.KING);
        squares[row-1][5] = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
        squares[row-1][6] = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
        squares[row-1][7] = new ChessPiece(color, ChessPiece.PieceType.ROOK);

        kingPos.put(color, new ChessPosition(row, 5));
    }

    private void makePawnRow(int row, ChessGame.TeamColor color) {
        for (int c = 0; c < size; c++) {
            squares[row - 1][c] = new ChessPiece(color, ChessPiece.PieceType.PAWN);
        }
    }

    public int getSize() {
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return (size == that.size && Objects.deepEquals(squares, that.squares));
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.deepHashCode(squares), size);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ChessPiece[] boardRow : squares) {
            sb.append("\n").append((Arrays.deepToString(boardRow)));
        }
        return sb.toString();

    }
}
