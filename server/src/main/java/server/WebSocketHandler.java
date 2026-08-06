package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.InvalidAuthTokenException;
import model.AuthData;
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
            String serializedServerMessage = serializer.toJson(new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, username + "has connected to the game"));
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
        System.out.println("Move made");
    }

    private void leaveGame(Session session, String username, UserGameCommand command) {
        System.out.println("Game left");
    }

    private void resign(Session session, String username, UserGameCommand command) {
        System.out.println("Resigned");
    }

    private String getUsername(String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            throw new InvalidAuthTokenException();
        }
        return authData.username();
    }

}