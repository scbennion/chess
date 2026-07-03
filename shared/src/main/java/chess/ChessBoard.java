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
    private HashMap<ChessPosition, ChessPiece> whitePieces;
    private HashMap<ChessPosition, ChessPiece> blackPieces;

    public ChessBoard() {
        squares = new ChessPiece[size][size];
        kingPos = new HashMap<>();
        whitePieces = new HashMap<>();
        blackPieces = new HashMap<>();
    }

    /**
     * Adds a chess piece to the chessboard
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece)  {
        squares[position.getRow()-1][position.getColumn()-1] = piece;

        ChessGame.TeamColor color = piece.getTeamColor();

        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            kingPos.put(color, position);
        }
        switch (color) {
            case WHITE -> whitePieces.put(position, piece);
            case BLACK -> blackPieces.put(position, piece);
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

    public HashMap<ChessPosition, ChessPiece> getSidePieces(ChessGame.TeamColor color) {
        return switch(color) {
            case WHITE -> whitePieces;
            case BLACK -> blackPieces;
        };
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
    Makes the back row
     */
    private void makeBackRow(int row, ChessGame.TeamColor color) {

        addPiece(new ChessPosition(row, 1), new ChessPiece(color, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(row, 2), new ChessPiece(color, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(row, 3), new ChessPiece(color, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(row, 4), new ChessPiece(color, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(row, 5), new ChessPiece(color, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(row, 6), new ChessPiece(color, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(row, 7), new ChessPiece(color, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(row, 8), new ChessPiece(color, ChessPiece.PieceType.ROOK));

        setKingPos(color, new ChessPosition(row, 5));
    }

    private void setKingPos (ChessGame.TeamColor color, ChessPosition pos) {
        kingPos.put(color, pos);
    }

    private void makePawnRow(int row, ChessGame.TeamColor color) {
        for (int c = 1; c <= size; c++) {
            addPiece(new ChessPosition(row, c), new ChessPiece(color, ChessPiece.PieceType.PAWN));
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
            for (ChessPiece p : boardRow) {
                if (p != null) {
                    sb.append(p).append("\t");
                } else {
                    sb.append("_____").append("\t");
                }
            }
            sb.append("\n");
            //sb.append("\n").append((Arrays.deepToString(boardRow)));
        }
        return sb.toString();

    }
}
