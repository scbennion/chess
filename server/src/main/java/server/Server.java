package server;

import io.javalin.*;
import io.javalin.http.Context;

public class Server {

    private final Javalin javalin;

    public Server() {
        ChessHandler handler = new ChessHandler();
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
                    ws.onConnect(ctx -> {
                        ctx.enableAutomaticPings();
                        System.out.println("Websocket connected");
                    });
                    ws.onMessage(ctx -> ctx.send("WebSocket response:" + ctx.message()));
                    ws.onClose(_ -> System.out.println("Websocket closed"));
                });
        // Register your endpoints and exception handlers here.

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
