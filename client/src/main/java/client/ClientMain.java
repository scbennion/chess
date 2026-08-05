package client;

import ui.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.util.Scanner;

import static ui.EscapeSequences.SET_TEXT_COLOR_WHITE;

public class ClientMain implements ServerMessageObserver {
    private static final int TEST_PORT = 8000;
    private static ServerFacade serverFacade;
    private static WSFacade wsFacade;

    public ClientMain(int port) throws Exception {
        serverFacade = new ServerFacade(port);
        wsFacade = new WSFacade(port);
    }


    public static void main(String[] args) throws Exception {
        new ClientMain(TEST_PORT);

        System.out.print(SET_TEXT_COLOR_WHITE);
        ReplUI ui = new PreLoginUI(serverFacade);
        System.out.println("♕ Welcome to 240 Chess Client. Type HELP to get started ♕\n");
        Scanner scanner = new Scanner(System.in);

        String eval = "";
        while (!eval.equals("quit")) {
            System.out.print(ui.prompt());
            String read = scanner.nextLine();
            eval = ui.eval(read);
            System.out.print(ui.print());

            switch (eval) {
                case "registered", "logged in", "game left" -> ui = new PostLoginUI(ui.getAuthToken(), serverFacade);
                case "logged out" -> ui = new PreLoginUI(serverFacade);
                case "game joined", "game observed" -> {
                    assert ui instanceof PostLoginUI;
                    ui = new GameplayUI(ui.getAuthToken(), ((PostLoginUI) ui).getConnectedGameID(), wsFacade);
                }
            }
        }
        System.exit(0);
    }


    @Override
    public void notify(ServerMessage message) {
        System.out.println(message.toString());
    }

    private void connectWebSocket(String authToken, int gameID) {
        new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
    }
}
