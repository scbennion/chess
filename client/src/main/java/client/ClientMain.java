package client;

import ui.*;

import java.util.Scanner;

import static ui.EscapeSequences.SET_TEXT_COLOR_WHITE;

public class ClientMain {
    public static void main(String[] args) {
        int port = 8080;
        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        }
        ServerFacade serverFacade = new ServerFacade(port);

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

            if (eval.equals("registered") || eval.equals("logged in")) {
                ui = new PostLoginUI(ui.getAuthToken());
            } else if (eval.equals("logged out")) {
                ui = new PreLoginUI();
            }
        }
        serverFacade.stop();
    }
}
