package client;

import chess.*;
import server.ServerFacade;
import ui.*;

import java.util.Scanner;

import static ui.EscapeSequences.SET_TEXT_COLOR_WHITE;

public class ClientMain {
    public static void main(String[] args) {
        System.out.print(SET_TEXT_COLOR_WHITE);
        ServerFacade serverFacade = new ServerFacade(8080);
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
        System.exit(0);
    }
}
