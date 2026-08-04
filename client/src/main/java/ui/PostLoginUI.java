package ui;

import chess.ChessBoard;
import chess.ChessGame;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.InvalidGameIDException;
import model.GameData;
import client.ServerFacade;

import java.util.HashMap;
import java.util.Map;

import static ui.EscapeSequences.RESET_TEXT_ITALIC;
import static ui.EscapeSequences.SET_TEXT_ITALIC;

public class PostLoginUI extends ReplUI {

    private GameplayUI gamePlayUI = new GameplayUI();
    private Map<Integer, Integer> gameIDTracker = new HashMap<>();

    public PostLoginUI(String authToken) {
        this.authToken = authToken;
    }

    @Override
    public String prompt() {
        return "[LOGGED IN] >>> ";
    }


    @Override
    public <T> String eval(String input, T connector) {
        ServerFacade serverFacade = (ServerFacade) connector;
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
        try {
            String[] splitted = input.split(WHITE_SPACE);
            int uiGameID = Integer.parseInt(splitted[1]);
            String color = splitted[2];
            if (!color.equalsIgnoreCase("WHITE") && !color.equalsIgnoreCase("BLACK")) {
                output = "unable to join game. Make sure you format your color as 'WHITE' or 'BLACK'\n";
                return false;
            }
            serverFacade.joinGame(authToken, color.toUpperCase(), uiGameID);
            ChessGame.TeamColor orientation = color.equalsIgnoreCase("WHITE") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
            output = "game joined and ready to play\n" + gamePlayUI.drawBoard(getSpecificBoard(uiGameID, serverFacade), orientation);
            return true;
        } catch (DataAccessException e) {
            output = "unable to join game. Make sure your game ID is correct and the player color is available\n";
        } catch (RuntimeException e) {
            output = "unable to join game. Make sure your game ID is an integer and you included your side color.\n";
        }
        return false;
    }

    private ChessBoard getSpecificBoard(int uiGameID, ServerFacade serverFacade) throws DataAccessException {
        //slightly inefficient because it gets all the games in the database
        //future implementations should be more specific for better performance
        GameData[] games = serverFacade.listGames(authToken);
        for (GameData gameData : games) {
            if (gameData.gameID() == gameIDTracker.get(uiGameID)) {
                return gameData.game().getBoard();
            }
        }
        throw new InvalidGameIDException();
    }

    private boolean observeGame(String input, ServerFacade serverFacade) {
        try {
            int uiGameID = Integer.parseInt(input.split(WHITE_SPACE)[1]);
            output = gamePlayUI.drawBoard(getSpecificBoard(uiGameID, serverFacade), ChessGame.TeamColor.WHITE);
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
