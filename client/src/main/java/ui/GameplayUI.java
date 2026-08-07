package ui;

import chess.*;
import client.WSFacade;

import java.util.ArrayList;

import static ui.EscapeSequences.*;

public class GameplayUI extends ReplUI {

    private final int gameID;
    private WSFacade wsFacade;
    private final String color;
    private ChessGame game;
    static final String CHESS_ALPHABET = "abcdefgh";
    static final String CHESS_NUMBERS = "12345678";

    public GameplayUI(String authToken, int gameID, String color, WSFacade wsFacade) {
        this.authToken = authToken;
        this.gameID = gameID;
        this.wsFacade = wsFacade;
        this.color = color;
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }

    @Override
    public String prompt() {
        return "[GAME] >>> ";
    }

    @Override
    public String eval(String input) {
        input = input.strip();
        if (input.equalsIgnoreCase("redraw")) {
            redraw();
            return "game redrawn";
        } else if (input.equalsIgnoreCase("leave")) {
            leaveGame();
            return "game left";
        } else if (input.toLowerCase().startsWith("make_move")) {
            return makeMove(input) ? "move made" : "failed";
        } else if (input.toLowerCase().startsWith("highlight")) {
            highlightLegalMoves(input);
            return highlightLegalMoves(input) ? "highlighted" : "failed";
        } else if (input.equalsIgnoreCase("resign")) {
            resign();
            return "resigned";
        } else {
            help();
            return "helped";
        }
    }

    public String redraw() {
        if (color == null) {
            output = drawBoard(game.getBoard(), ChessGame.TeamColor.WHITE);
        } else {
            output = drawBoard(game.getBoard(), ChessGame.TeamColor.valueOf(color.toUpperCase()));
        }
        return output;
    }

    private void leaveGame() {
        try {
            wsFacade.leaveGame(authToken, gameID);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        output = "you have left the game\n";
    }

    private void resign() {
        try {
            wsFacade.resign(authToken, gameID, color);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        output = "resigned\n";
    }


    private boolean makeMove(String input) {
        try {
            String[] splitted = getStrings(input, 2);
            if (splitted == null) {
                return false;
            }

            ChessPiece.PieceType promotionType = null;
            if (splitted.length == 4) {
                promotionType = ChessPiece.PieceType.valueOf(splitted[3].toUpperCase());
            }
            ChessMove move = new ChessMove(convertUIPos(splitted[1]), convertUIPos(splitted[2]), promotionType);
            wsFacade.makeMove(authToken, gameID, move, color);
            output = "move sent to server\n";
            return true;

        } catch (IllegalArgumentException e) {
            output = "make sure your promotion type is a valid chess piece type (ex: queen)\n";
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String[] getStrings(String input, int numberOfPositions) {
        String[] splitted = input.split(WHITE_SPACE);
        for (int i = 0; i < numberOfPositions; i++) {
            if (!checkPosFormatting(splitted[i + 1])) {
                output = """
                        make sure your positions are formatted <letter><number> (ex: a1)
                        If your move includes promotion, include the piece name at the end as a separate word
                        """;
                return null;
            }
        }
        ChessPiece p = game.getBoard().getPiece(convertUIPos(splitted[1]));
        if (p == null) {
            output = "no piece at specified game position\n";
            return null;
        }
        return splitted;
    }

    public boolean highlightLegalMoves(String input) {
        String[] splitted = getStrings(input, 1);
        if (splitted == null) {
            return false;
        }
        var validMoves = game.validMoves(convertUIPos(splitted[1]));
        ArrayList<ChessPosition> validPositions = new ArrayList<>();
        for (ChessMove move : validMoves) {
            validPositions.add(move.getEndPosition());
        }
        if (color == null) {
            output = drawBoardAndHighlights(game.getBoard(), ChessGame.TeamColor.WHITE, validPositions);
        } else {
            output = drawBoardAndHighlights(game.getBoard(), ChessGame.TeamColor.valueOf(color), validPositions);
        }
        return true;
    }

    private boolean checkPosFormatting(String uiPos) {
        return (CHESS_ALPHABET.contains(uiPos.substring(0, 1)) && CHESS_NUMBERS.contains(uiPos.substring(1, 2)) && uiPos.length() == 2);
    }

    private ChessPosition convertUIPos(String uiPos) {
        return new ChessPosition(Integer.parseInt(uiPos.substring(1)), CHESS_ALPHABET.indexOf(uiPos.charAt(0)) + 1);
    }

    private void help() {
        output = String.format("""
                HELP\t%1$slist possible game commands%2$s
                REDRAW\t%1$sredraw board%2$s
                LEAVE\t%1$sleave game%2$s
                MAKE_MOVE <POSITION> <POSITION>\t%1$sredraw board%2$s
                HIGHLIGHT <POSITION>\t%1$shighlights legal moves for a piece%2$s
                RESIGN\t%forfeit the game%2$s
                """, SET_TEXT_ITALIC, RESET_TEXT_ITALIC);
    }

    private String drawBoard(ChessBoard board, ChessGame.TeamColor orientation) {
        return drawBoardAndHighlights(board, orientation, null);
    }

    private String drawBoardAndHighlights(ChessBoard board, ChessGame.TeamColor orientation, ArrayList<ChessPosition> highlights) {
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
                output.append(drawSquare(row, col, piece, color, highlights));
            }
            output.append(rowIndicator).append("\n");
        }
        output.append(colIndicator);
        output.append(RESET_BG_COLOR).append(SET_TEXT_COLOR_WHITE);
        return output.toString();
    }

    private String drawSquare(int row, int col, ChessPiece piece, ChessGame.TeamColor color, ArrayList<ChessPosition> highlights) {
        String squareColor = null;
        if (highlights != null && highlights.contains(new ChessPosition(row, col))) {
            squareColor = SET_BG_COLOR_YELLOW;
        } else {
            squareColor = SET_BG_COLOR_WHITE;
            if ((col + row) % 2 == 0) {
                squareColor = SET_BG_COLOR_BLACK;
            }
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
