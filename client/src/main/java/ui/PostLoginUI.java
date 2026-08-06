package ui;

import chess.ChessGame;
import client.WSFacade;
import dataaccess.exceptions.DataAccessException;
import model.GameData;
import client.ServerFacade;

import java.util.HashMap;
import java.util.Map;

import static ui.EscapeSequences.RESET_TEXT_ITALIC;
import static ui.EscapeSequences.SET_TEXT_ITALIC;

public class PostLoginUI extends ReplUI {

    private final Map<Integer, Integer> gameIDTracker = new HashMap<>();
    private int connectedGameID = -1;
    private final ServerFacade serverFacade;
    private final WSFacade wsFacade;
    private String color = null;

    public PostLoginUI(String authToken, ServerFacade serverFacade, WSFacade wsFacade) {
        this.authToken = authToken;
        this.serverFacade = serverFacade;
        this.wsFacade = wsFacade;
    }

    @Override
    public String prompt() {
        return "[LOGGED IN] >>> ";
    }

    @Override
    public String eval(String input) {
        input = input.strip();
        if (input.toLowerCase().startsWith("create")) {
            boolean successfulCreation = createGame(input);
            return successfulCreation ? "game created" : "failed";
        } else if (input.toLowerCase().startsWith("join")) {
            boolean successfulJoin = joinGame(input);
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
            return "helped";
        }
    }

    public int getConnectedGameID() {
        return connectedGameID;
    }

    public String getColor() {
        return color;
    }

    private boolean createGame(String input) {
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

    /**
     * attempts to join a game using uiGameID and player color
     * Calls ServerFacade to join the game
     * Calls WSFacade to connect to the game with a WebSocket
     *
     * @param input user join game input
     * @return stored gameID or -1 if unsuccessful
     */
    private boolean joinGame(String input) {
        try {
            String[] splitted = input.split(WHITE_SPACE);
            int uiGameID = Integer.parseInt(splitted[1]);
            color = splitted[2].toLowerCase();
            if (!color.equals("white") && !color.equals("black")) {
                output = "unable to join game. Make sure you format your color as 'WHITE' or 'BLACK'\n";
                return false;
            }
            connectedGameID = gameIDTracker.get(uiGameID);
            serverFacade.joinGame(authToken, color.toUpperCase(), connectedGameID);
            wsFacade.connect(authToken, connectedGameID, color);
            output = "game joined and ready to play\n";
            return true;
        } catch (DataAccessException e) {
            output = "unable to join game. Make sure your game ID is correct and the player color is available\n";
            return false;
        } catch (Exception e) {
            //note: you should not be able to join a game until the temporary uiGameID is created using listGames
            output = "unable to join game. Make sure your game ID is an integer and you included your side color.\n";
            return false;
        }
    }

    private boolean observeGame(String input, ServerFacade serverFacade) {
        try {
            int uiGameID = Integer.parseInt(input.split(WHITE_SPACE)[1]);
            connectedGameID = gameIDTracker.get(uiGameID);
            output = "game is now being observed\n";
            wsFacade.connect(authToken, connectedGameID, null);
            return true;
        } catch (Exception e) {
            output = "unable to observe game. Make sure your game ID is correct.\n";
            return false;
        }
    }

    private boolean listGames(ServerFacade serverFacade) {
        try {
            StringBuilder sb = new StringBuilder();
            GameData[] games = serverFacade.listGames(authToken);
            for (int i = 0; i < games.length; i++) {
                sb.append(String.format("%s: %s. %s White Player: %s Black Player: %s%s\n", i + 1, games[i].gameName(),
                        SET_TEXT_ITALIC, games[i].whiteUsername(), games[i].blackUsername(), RESET_TEXT_ITALIC));
                gameIDTracker.put(i + 1, games[i].gameID());
            }
            output = sb.toString();
            return true;
        } catch (DataAccessException e) {
            output = "game listing error\n";
            return false;
        }
    }

    private boolean logout(ServerFacade serverFacade) {
        try {
            serverFacade.logout(authToken);
            output = "logged out\n";
            return true;
        } catch (DataAccessException e) {
            output = "unable to logout. YOU'RE TRAPPED HERE WITH ME FOREVER\n";
            return false;
        }
    }

    private void help() {
        output = """
                 possible commands:
                 create <GAME_NAME>
                 join <ID> <WHITE|BLACK>
                 observe <ID>
                 list
                 logout
                 quit
                 help
                """;
    }

}
