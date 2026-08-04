package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import client.ServerFacade;
import client.WSFacade;

import static ui.EscapeSequences.*;

public class GameplayUI extends ReplUI {

//    Help	Displays text informing the user what actions they can take.
//    Redraw Chess Board	Redraws the chess board upon the user’s request.
//    Leave	Removes the user from the game (whether they are playing or observing the game). The client transitions back to the Post-Login UI.
//    Make Move	Allow the user to input what move they want to make. The board is updated to reflect the result of the move, and the board automatically updates on all clients involved in the game.
//    Resign	Prompts the user to confirm they want to resign. If they do, the user forfeits the game and the game is over. Does not cause the user to leave the game.
//    Highlight Legal Moves	Allows the user to input the piece for which they want to highlight legal moves. The selected piece’s current square and all squares it can legally move to are highlighted. This is a local operation and has no effect on remote users’ screens.

    public GameplayUI(String authToken) {
        this.authToken = authToken;
    }

    @Override
    public String prompt() {
        return "[GAME] >>> ";
    }

    @Override
    public <T> String eval(String input, T connector) {
        ServerFacade serverFacade = (ServerFacade) connector;
        input = input.strip();
        if (input.equalsIgnoreCase("redraw")) {
            return "game redrawn";
        } else if (input.equalsIgnoreCase("leave")) {
            return "game left";
        } else if (input.toLowerCase().startsWith("MAKE_MOVE")) {
            return "move made";
        } else if (input.toLowerCase().startsWith("HIGHLIGHT")) {
            return "highlighted";
        } else {
            help();
            return "helped";
        }
    }

    private void help() {
        output = String.format("""
                HELP\t%1$slist possible game commands%2$s
                REDRAW\t%1$sredraw board%2$s
                LEAVE\t%1$sleave game%2$s
                MAKE_MOVE <POSITION> <POSITION>\t%1$sredraw board%2$s
                HIGHLIGHT <POSITION>\t%1$shighlights legal moves for a piece%2$s
                """, SET_TEXT_ITALIC, RESET_TEXT_ITALIC);
    }

    public String drawBoard(ChessBoard board, ChessGame.TeamColor orientation) {
        StringBuilder output = new StringBuilder();
        var whitePieceMap = board.getSidePieces(ChessGame.TeamColor.WHITE);
        var blackPieceMap = board.getSidePieces(ChessGame.TeamColor.BLACK);
        final String alphabet = orientation == ChessGame.TeamColor.WHITE ? "a  b  c  d  e  f  g  h" : "h  g  f  e  d  c  b  a";
        String colIndicator = String.format("%s%s    %s    %s\n", SET_BG_COLOR_LIGHT_GREY, SET_TEXT_COLOR_BLACK, alphabet, RESET_BG_COLOR);
        output.append(colIndicator);
        for (int y = 8; y >= 1; y--) {
            int row = orientation == ChessGame.TeamColor.WHITE ? y : 9 - y;
            String rowIndicator = String.format("%s%s %s %s", SET_BG_COLOR_LIGHT_GREY, SET_TEXT_COLOR_BLACK, row, RESET_BG_COLOR);
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
}
