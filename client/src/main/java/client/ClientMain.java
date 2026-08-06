package client;

import chess.ChessGame;
import ui.*;
import websocket.ServerMessageObserver;
import websocket.messages.ServerMessage;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ClientMain implements ServerMessageObserver {
    private static final int TEST_PORT = 8000;
    private static ServerFacade serverFacade;
    private static WSFacade wsFacade;
    private static ReplUI ui = null;
    private static ChessGame game = null;

    public ClientMain(int port) throws Exception {
        serverFacade = new ServerFacade(port);
        wsFacade = new WSFacade(port, this);
    }

    public static void main(String[] args) throws Exception {
        new ClientMain(TEST_PORT);

        System.out.print(SET_TEXT_COLOR_WHITE);
        ui = new PreLoginUI(serverFacade);
        System.out.println("♕ Welcome to 240 Chess Client. Type HELP to get started ♕\n");
        Scanner scanner = new Scanner(System.in);

        String eval = "";
        while (!eval.equals("quit")) {
            System.out.print(ui.prompt());
            String read = scanner.nextLine();
            eval = ui.eval(read);
            System.out.print(ui.print());

            switch (eval) {
                case "registered", "logged in", "game left" ->
                        ui = new PostLoginUI(ui.getAuthToken(), serverFacade, wsFacade);
                case "logged out" -> ui = new PreLoginUI(serverFacade);
                case "game joined", "game observed" -> {
                    assert ui instanceof PostLoginUI;
                    int gameID = ((PostLoginUI) ui).getConnectedGameID();
                    ui = new GameplayUI(ui.getAuthToken(), gameID, ((PostLoginUI) ui).getColor(), wsFacade);
                }
            }
        }
        System.exit(0);
    }


    @Override
    public void notify(ServerMessage serverMessage) {
        switch (serverMessage.getServerMessageType()) {
            case LOAD_GAME -> {
                game = serverMessage.getGame();
                game.getBoard().buildPieceCaches();
                assert (ui instanceof GameplayUI);
                GameplayUI gameplayUI = (GameplayUI) ui;
                gameplayUI.setGame(game);
                System.out.print("\n" + gameplayUI.redraw());
                System.out.print(ui.prompt());
            }
            case ERROR -> System.out.print(SET_TEXT_COLOR_RED + serverMessage.getError() + SET_TEXT_COLOR_WHITE
                    + "\n" + ui.prompt());
            case NOTIFICATION ->
                    System.out.print(SET_TEXT_COLOR_LIGHT_GREY + serverMessage.getMessage() + SET_TEXT_COLOR_WHITE + "\n" + ui.prompt());
        }
    }
}
