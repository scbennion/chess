package client;

import chess.*;
import server.ServerFacade;
import ui.PreLoginUI;

import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) {
        System.out.println("♕ Welcome to 240 Chess Client. Type HELP to get started ♕\n");

        PreLoginUI ui = new PreLoginUI();
        Scanner scanner = new Scanner(System.in);
        //ServerFacade serverFacade = new ServerFacade()

        String eval = "";
        while (!eval.equals("quit")) {
            System.out.print(ui.prompt());
            String read = scanner.nextLine();
            eval = ui.eval(read);
            System.out.println(ui.print());
        }
    }
}
