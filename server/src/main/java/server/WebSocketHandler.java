package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;

import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.jetbrains.annotations.NotNull;
import websocket.commands.UserGameCommand;


public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final Gson serializer = new Gson();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext wsMessageContext) throws Exception {
        int gameId = -1;
        Session session = wsMessageContext.session;

        try {
            UserGameCommand command = serializer.fromJson(
                    wsMessageContext.message(), UserGameCommand.class);
            gameId = command.getGameID();
            String username = getUsername(command.getAuthToken());
            saveSession(gameId, session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, (UserGameCommand) command);
                case MAKE_MOVE -> makeMove(session, username, (UserGameCommand) command);
                case LEAVE -> leaveGame(session, username, (UserGameCommand) command);
                case RESIGN -> resign(session, username, (UserGameCommand) command);
                default -> System.out.println("invalid move command");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            sendMessage(session, gameId, "Error: " + ex.getMessage());
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private String getUsername(String authToken) {
        return "username placeholder";
    }

    private void saveSession(int gameID, Session session) {
        System.out.println("Session saved");
    }

    private void sendMessage(Session session, int gameID, String msg) throws Exception {
        throw new Exception(msg);
    }

    private void connect(Session session, String username, UserGameCommand command) {
        System.out.println("Connected");
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

}