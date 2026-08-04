package client;

import ui.*;
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
        ReplUI ui = new PreLoginUI();
        System.out.println("♕ Welcome to 240 Chess Client. Type HELP to get started ♕\n");
        Scanner scanner = new Scanner(System.in);

        String eval = "";
        while (!eval.equals("quit")) {
            System.out.print(ui.prompt());
            String read = scanner.nextLine();
            eval = ui.eval(read, serverFacade);
            System.out.print(ui.print());

            if (eval.equals("registered") || eval.equals("logged in") || eval.equals("game left")) {
                ui = new PostLoginUI(ui.getAuthToken());
            } else if (eval.equals("logged out")) {
                ui = new PreLoginUI();
            } else if (eval.equals("game joined") || eval.equals("game observed")) {
                ui = new GameplayUI(ui.getAuthToken());
            }
        }
        System.exit(0);
    }


    @Override
    public void notify(ServerMessage message) {
        System.out.println(message.toString());
    }
}
