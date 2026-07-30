package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;

public class GameplayUI {

    public String drawBoard(ChessBoard board, ChessGame.TeamColor orientation) {
        StringBuilder output = new StringBuilder();
        var whitePieceMap = board.getSidePieces(ChessGame.TeamColor.WHITE);
        var blackPieceMap = board.getSidePieces(ChessGame.TeamColor.BLACK);
        final String alphabet = orientation == ChessGame.TeamColor.WHITE ? "a  b  c  d  e  f  g  h" : "h  g  f  e  d  c  b  a";
        String colIndicator = String.format("%s%s    %s    \n", SET_BG_COLOR_LIGHT_GREY, SET_TEXT_COLOR_BLACK, alphabet);
        output.append(colIndicator);
        for (int y = 8; y >= 1; y--) {
            int row = orientation == ChessGame.TeamColor.WHITE ? y : 9 - y;
            String rowIndicator = String.format("%s%s %s ", SET_BG_COLOR_LIGHT_GREY, SET_TEXT_COLOR_BLACK, row);
            output.append(rowIndicator);
            for (int x = 1; x <= 8; x++) {
                int col = orientation == ChessGame.TeamColor.WHITE ? x : 9 - x;
                ChessPiece piece = null;
                ChessGame.TeamColor color = null;
                ChessPosition pos = new ChessPosition(row, col);
                if (whitePieceMap.get(pos) != null) {
                    piece = whitePieceMap.get(pos);
                    color = ChessGame.TeamColor.WHITE;
                } else if (blackPieceMap.get(pos) != null) {
                    piece = blackPieceMap.get(pos);
                    color = ChessGame.TeamColor.BLACK;
                }
                output.append(drawSquare(row, col, piece, color));
            }
            output.append(rowIndicator).append("\n");
        }
        output.append(colIndicator);
        output.append(SET_BG_COLOR_BLACK).append(SET_TEXT_COLOR_WHITE);
        return output.toString();
    }

    private String drawSquare(int row, int col, ChessPiece piece, ChessGame.TeamColor color) {
        String squareColor = SET_BG_COLOR_WHITE;
        if ((col + row) % 2 == 0) {
            squareColor = SET_BG_COLOR_BLACK;
        }

        String pieceLetter = " ";
        String pieceColor = "";
        switch (color) {
            case WHITE -> {
                pieceLetter = pieceTypeLetter(piece.getPieceType());
                pieceColor = SET_TEXT_COLOR_RED;
            }
            case BLACK -> {
                pieceLetter = pieceTypeLetter(piece.getPieceType());
                pieceColor = SET_TEXT_COLOR_BLUE;
            }
            case null -> {
            }
        }

        return String.format("%s%s %s ", squareColor, pieceColor, pieceLetter);

    }

    private String pieceTypeLetter(ChessPiece.PieceType type) {
        return switch (type) {
            case KING -> "K";
            case QUEEN -> "Q";
            case BISHOP -> "B";
            case KNIGHT -> "N";
            case ROOK -> "R";
            case PAWN -> "P";
        };
    }

    public static void main(String[] args) {
        ChessBoard testBoard = new ChessBoard();
        testBoard.resetBoard();
        System.out.println(new GameplayUI().drawBoard(testBoard, ChessGame.TeamColor.BLACK));
    }

}
