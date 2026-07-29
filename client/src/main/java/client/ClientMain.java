package client;

import chess.*;
import server.Server;
import server.ServerFacade;
import ui.*;

import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) {
        int port = 8080;
        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        }

        Server server = new Server();
        port = server.run(port);
        System.out.println("Started test HTTP server on " + port);
        ServerFacade serverFacade = new ServerFacade(port);
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
