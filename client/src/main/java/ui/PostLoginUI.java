package ui;

import server.ServerFacade;

public class PostLoginUI extends ReplUI {

    public PostLoginUI(String authToken) {
        this.authToken = authToken;
    }

    @Override
    public String prompt() {
        return "[LOGGED IN] >>> ";
    }

    @Override
    public String eval(String input, ServerFacade serverFacade) {
        input = input.strip();
        if (input.toLowerCase().startsWith("create")) {
            boolean successfulCreation = createGame(input, serverFacade);
            return successfulCreation ? "game created" : "failed";
        } else if (input.toLowerCase().startsWith("join")) {
            boolean successfulJoin = joinGame(input, serverFacade);
            return successfulJoin ? "game joined" : "failed";
        } else if (input.toLowerCase().startsWith("observe")) {
            boolean successfulObserve = observeGame(input, serverFacade);
            return successfulObserve ? "game observed" : "failed";
        } else if (input.equalsIgnoreCase("quit")) {
            quit();
            return "quit";
        } else if (input.equalsIgnoreCase("list")) {
            boolean successfulList = listGames(serverFacade);
            return successfulList ? "games listed" : "failed";
        } else if (input.equalsIgnoreCase("logout")) {
            boolean successfulLogout = logout(serverFacade);
            return successfulLogout ? "logged out" : "failed";
        } else {
            help();
            return "help";
        }
    }

    private boolean createGame(String input, ServerFacade serverFacade) {
        output = "game created";
        return true;
    }

    private boolean joinGame(String input, ServerFacade serverFacade) {
        output = "game joined";
        return true;
    }

    private boolean observeGame(String input, ServerFacade serverFacade) {
        output = "game observed";
        return true;
    }

    private boolean listGames(ServerFacade serverFacade) {
        output = "games listed";
        return true;
    }

    private boolean logout(ServerFacade serverFacade) {
        output = "logged out";
        return true;
    }

    private void help() {
        output = "helped";
    }

}
