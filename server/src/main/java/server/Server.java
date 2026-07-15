package server;

import io.javalin.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        ChessHandler handler = new ChessHandler();
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", handler::processRegister);
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
