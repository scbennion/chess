package client;

import chess.ChessMove;
import com.google.gson.Gson;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import websocket.ServerMessageObserver;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;

public class WSFacade extends Endpoint {
    public Session session;
    private final ServerMessageObserver listener;
    private final Gson serializer = new Gson();

    public WSFacade(int port, ServerMessageObserver listener) throws Exception {
        String url = "ws://localhost:" + port + "/ws";
        URI socketURI = new URI(url);
        this.listener = listener;

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, socketURI);

        //set message handler
        session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                handleMessage(message);
            }
        });
    }

    public void connect(String authToken, int gameID, String color) throws Exception {
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            command.setColor(color);
            send(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public void makeMove(String authToken, int gameID, ChessMove move, String color) {
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID);
            command.setMove(move);
            command.setColor(color);
            send(new Gson().toJson(command));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void leaveGame(String authToken, int gameID) throws Exception {
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            send(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    private void handleMessage(String messageString) {
        try {
            ServerMessage message = serializer.fromJson(messageString, ServerMessage.class);
            listener.notify(message);
        } catch (Exception ex) {
            listener.notify(new ServerMessage(ServerMessage.ServerMessageType.ERROR, ex.getMessage()));
        }
    }

    private void send(String message) throws IOException {
        session.getBasicRemote().sendText(message);
    }

    // This method must be overridden, but we don't have to do anything with it
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}