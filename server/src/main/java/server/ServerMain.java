package server;

import chess.*;

public class ServerMain {
    public static void main(String[] args) {
        Server server = new Server();
        int port = server.run(8000);
        System.out.println("♕ 240 Chess Server");
    }
}
