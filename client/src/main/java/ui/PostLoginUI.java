package ui;

import dataaccess.exceptions.DataAccessException;
import model.GameData;
import server.ServerFacade;

import static ui.EscapeSequences.RESET_TEXT_ITALIC;
import static ui.EscapeSequences.SET_TEXT_ITALIC;

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
        try {
            String gameName = input.split(WHITE_SPACE)[1];
            serverFacade.createGame(authToken, gameName);
            output = gameName + " game created\n";
            return true;
        } catch (DataAccessException e) {
            output = "game creation error\n";
            return false;
        } catch (Exception e) {
            output = "bad game name\n";
            return false;
        }
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
        try {
            StringBuilder sb = new StringBuilder();
            GameData[] games = serverFacade.listGames(authToken);
            for (int i = 0; i < games.length; i++) {
                sb.append(String.format("%s: %s. %s White Player: %s Black Player: %s%s\n", i, games[i].gameName(),
                        SET_TEXT_ITALIC, games[i].whiteUsername(), games[i].blackUsername(), RESET_TEXT_ITALIC));
            }
            output = sb.toString();
            return true;
        } catch (DataAccessException e) {
            output = "game listing error\n";
            return false;
        }
    }

    private boolean logout(ServerFacade serverFacade) {
        output = "logged out\n";
        return true;
    }

    private void help() {
        output = """
                 possible commands:
                 create <GAME_NAME>
                 join <ID> <WHITE|BLACK>
                 observe <ID>
                 logout
                 quit
                 help
                """;
    }

}
