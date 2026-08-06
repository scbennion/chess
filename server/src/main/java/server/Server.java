package server;

import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;

public class Server {

    private final Javalin javalin;

    public Server() {
        GameDAO gameDAO = new SQLGameDAO();
        UserDAO userDAO = new SQLUserDAO();
        AuthDAO authDAO = new SQLAuthDAO();

        ChessHandler handler = new ChessHandler(gameDAO, authDAO, userDAO);
        WebSocketHandler webSocketHandler = new WebSocketHandler(gameDAO, authDAO, userDAO);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", handler::processRegister)
                .post("/session", handler::processLogin)
                .delete("/session", handler::processLogout)
                .get("/game", handler::processListGames)
                .post("/game", handler::processCreateGame)
                .put("/game", handler::processJoinGame)
                .delete("/db", handler::processClear)
                .exception(Exception.class, handler::exceptionHandler)
                .ws("/ws", ws -> {
                    ws.onConnect(webSocketHandler);
                    ws.onMessage(webSocketHandler);
                    ws.onClose(webSocketHandler);
                });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
