package server;

import chess.ChessGame;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.InvalidAuthTokenException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;

import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.jetbrains.annotations.NotNull;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;


public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connectionManager = new ConnectionManager();
    private final Gson serializer = new Gson();
    GameDAO gameDAO;
    AuthDAO authDAO;
    UserDAO userDAO;
    static final String CHESS_ALPHABET = "abcdefgh";

    public WebSocketHandler(GameDAO gameDAO, AuthDAO authDAO, UserDAO userDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext wsMessageContext) {
        Session session = wsMessageContext.session;

        try {
            UserGameCommand command = serializer.fromJson(
                    wsMessageContext.message(), UserGameCommand.class);
            String username = getUsername(command.getAuthToken());

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, command);
                case MAKE_MOVE -> makeMove(session, username, command);
                case LEAVE -> leaveGame(session, username, command);
                case RESIGN -> resign(session, username, command);
                default -> System.out.println("invalid move command");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            notifySession(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Error: " + ex.getMessage()));
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void notifySession(Session session, ServerMessage serverMessage) {
        try {
            session.getRemote().sendString(serializer.toJson(serverMessage));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void connect(Session session, String username, UserGameCommand command) {
        try {
            connectionManager.add(command.getGameID(), session);

            ChessGame game = gameDAO.getGame(command.getGameID()).game();
            ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
            notifySession(session, serverMessage);

            String broadcastMessage = username + " has connected to the game as an observer";
            if (command.getColor() != null) {
                broadcastMessage = username + " has connected to the game as " + command.getColor().toLowerCase();
            }
            String serializedServerMessage = serializer.toJson(new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, broadcastMessage));
            connectionManager.broadcast(command.getGameID(), session, serializedServerMessage);
        } catch (DataAccessException e) {
            String msg = "Error: Game does not exist";
            notifySession(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR, msg));
        } catch (IOException e) {
            String msg = "Error: issues broadcasting" + e.getMessage();
            notifySession(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR, msg));
        }
    }

    private void makeMove(Session session, String username, UserGameCommand command) {
        GameData gameData = getGameData(command.getGameID(), session);
        if (gameData != null) {
            if (!gameData.game().isGameOver()) {
                if (isPlayer(gameData, username, session)) {
                    computeMakeMove(session, username, command, gameData);
                }
            } else {
                String msg = "Error: Game Over";
                notifySession(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR, msg));
            }
        }
    }

    private void computeMakeMove(Session session, String username, UserGameCommand command, GameData gameData) {
        try {
            ChessGame game = gameData.game();
            ChessGame.TeamColor color = ChessGame.TeamColor.BLACK;
            if (gameData.whiteUsername().equals(username)) {
                color = ChessGame.TeamColor.WHITE;
            }
            if (!color.equals(gameData.game().getTeamTurn())) {
                String msg = "Error: Not your turn";
                notifySession(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR, msg));
                return;
            }
            game.makeMove(command.getMove());
            notifySession(session, new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, game));
            String broadcastMessage = username + " has moved " + convertToUI(command.getMove().getStartPosition())
                    + " to " + convertToUI(command.getMove().getEndPosition());
            broadcastNotification(broadcastMessage, command.getGameID(), session);
            broadcastLoadGame(game, command.getGameID(), session);
            gameDAO.updateGameData(gameData);
            checkSpecialConditions(username, color, command, game, gameData);
        } catch (InvalidMoveException e) {
            String msg = "Error: Invalid Move";
            notifySession(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR, msg));
        } catch (DataAccessException e) {
            String msg = "Error: Unable to save updated game to database";
            notifySession(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR, msg));
        }
    }

    private void checkSpecialConditions(String username, ChessGame.TeamColor color, UserGameCommand command, ChessGame game, GameData gameData) {
        String broadcastMessage;
        if (game.isInCheckmate(game.oppositeColor(color))) {
            if (color == ChessGame.TeamColor.BLACK) {
                broadcastMessage = gameData.whiteUsername() + " is checkmated.";
            } else {
                broadcastMessage = gameData.blackUsername() + " is checkmated.";
            }
            broadcastNotification(broadcastMessage, command.getGameID(), null);
            game.setGameOver(true);
        } else if (game.isInCheck(game.oppositeColor(color))) {
            if (color == ChessGame.TeamColor.BLACK) {
                broadcastMessage = gameData.whiteUsername() + " is in check.";
            } else {
                broadcastMessage = gameData.blackUsername() + " is in check.";
            }
            broadcastNotification(broadcastMessage, command.getGameID(), null);
        } else if (game.isInStalemate(game.oppositeColor(color))) {
            broadcastMessage = "stalemate";
            broadcastNotification(broadcastMessage, command.getGameID(), null);
            game.setGameOver(true);
        }
    }

    private void resign(Session session, String username, UserGameCommand command) {
        GameData gameData = getGameData(command.getGameID(), session);
        if (gameData != null) {
            if (!gameData.game().isGameOver()) {
                if (isPlayer(gameData, username, session)) {
                    gameData.game().resign();
                    String broadcastMessage = username + " has resigned";
                    broadcastNotification(broadcastMessage, command.getGameID(), null);
                    try {
                        gameDAO.updateGameData(gameData);
                    } catch (DataAccessException e) {
                        String msg = "Error: Unable to save updated game to database";
                        notifySession(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR, msg));
                    }
                }
            } else {
                String msg = "Error: Game Over";
                notifySession(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR, msg));
            }
        }
    }


    private void broadcastNotification(String msg, int gameID, Session session) {
        try {
            String serializedBroadcastMessage = serializer.toJson(new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg));
            connectionManager.broadcast(gameID, session, serializedBroadcastMessage);
        } catch (IOException e) {
            String errorMsg = "Error: issues broadcasting\n" + e.getMessage();
            notifySession(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR, errorMsg));
        }
    }

    private void broadcastLoadGame(ChessGame game, int gameID, Session session) {
        try {
            String serializedBroadcastMessage = serializer.toJson(new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, game));
            connectionManager.broadcast(gameID, session, serializedBroadcastMessage);
        } catch (IOException e) {
            String errorMsg = "Error: issues broadcasting\n" + e.getMessage();
            notifySession(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR, errorMsg));
        }
    }

    private boolean isPlayer(GameData gameData, String username, Session session) {
        if ((gameData.whiteUsername() != null && gameData.whiteUsername().equals(username))
                || (gameData.blackUsername() != null && gameData.blackUsername().equals(username))) {
            return true;
        }
        String msg = "Error: Observer cannot interact with the game";
        notifySession(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR, msg));
        return false;
    }

    private GameData getGameData(int gameID, Session session) {
        try {
            return gameDAO.getGame(gameID);
        } catch (DataAccessException e) {
            String msg = "Error: Game does not exist";
            notifySession(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR, msg));
            return null;
        }
    }

    private String convertToUI(ChessPosition pos) {
        return CHESS_ALPHABET.charAt(pos.getColumn() - 1) + String.valueOf(pos.getRow());
    }

    private void leaveGame(Session session, String username, UserGameCommand command) {
        connectionManager.remove(command.getGameID(), session);
        GameData gameData = null;
        try {
            gameData = gameDAO.getGame(command.getGameID());
            if (gameData.whiteUsername() != null && gameData.whiteUsername().equals(username)) {
                gameDAO.updateGameData(new GameData(gameData.gameID(), null, gameData.blackUsername(), gameData.gameName(), gameData.game()));
            } else if (gameData.blackUsername() != null && gameData.blackUsername().equals(username)) {
                gameDAO.updateGameData(new GameData(gameData.gameID(), gameData.whiteUsername(), null, gameData.gameName(), gameData.game()));
            }
        } catch (DataAccessException e) {

        }
        broadcastNotification(username + " has left the game", command.getGameID(), session);
    }

    private String getUsername(String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            throw new InvalidAuthTokenException();
        }
        return authData.username();
    }

}