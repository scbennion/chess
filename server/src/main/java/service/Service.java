package service;

import chess.ChessGame.TeamColor;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.jetbrains.annotations.NotNull;

public class Service {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public Service() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
    }

    public AuthData register(UserData registerRequest) throws DataAccessException {
        if (registerRequest == null || registerRequest.username() == null
                || registerRequest.password() == null || registerRequest.email() == null) {
            throw new BadRequestException();
        }
        if (userDAO.getUser(registerRequest.username()) != null) {
            throw new AlreadyTakenException();
        }
        userDAO.createUser(registerRequest);
        return authDAO.createAuth(registerRequest.username());
    }

    public AuthData login(UserData loginRequest) throws DataAccessException {
        if (loginRequest.username() == null || loginRequest.password() == null || loginRequest.email() != null) {
            throw new BadRequestException();
        }
        UserData userData = userDAO.getUser(loginRequest.username());
        if (userData == null) {
            throw new InvalidUsernameException();
        }
        if (!userData.password().equals(loginRequest.password())) {
            throw new InvalidPasswordException();
        }
        return authDAO.createAuth(userData.username());
    }

    public void logout(String authToken) throws DataAccessException {
        getAuthData(authToken);
        authDAO.deleteAuth(authToken);
    }

    public GameData[] listGames(String authToken) throws DataAccessException {
        getAuthData(authToken);
        return gameDAO.listGames();
    }

    public int createGame(String authToken, String gameName) throws DataAccessException {
        getAuthData(authToken);
        GameData gameData = gameDAO.createGame(gameName);
        return gameData.gameID();
    }

    public void joinGame(String authToken, String colorString, int gameID) throws DataAccessException {
        AuthData authData = getAuthData(authToken);

        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            throw new InvalidGameIDException();
        }

        TeamColor color;
        try {
            color = TeamColor.valueOf(colorString);
        } catch (Exception e) {
            throw new BadRequestException();
        }

        switch (color) {
            case WHITE -> {
                if (gameData.whiteUsername() != null) {
                    throw new AlreadyTakenException();
                } else {
                    gameData = new GameData(gameData.gameID(), authData.username(), gameData.blackUsername(), gameData.gameName(), gameData.game());
                }
            }
            case BLACK -> {
                if (gameData.blackUsername() != null) {
                    throw new AlreadyTakenException();
                } else {
                    gameData = new GameData(gameData.gameID(), gameData.whiteUsername(), authData.username(), gameData.gameName(), gameData.game());
                }
            }
        }

        gameDAO.updateGameData(gameData);
    }

    @NotNull
    private AuthData getAuthData(String authToken) throws InvalidAuthTokenException {
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            throw new InvalidAuthTokenException();
        }
        return authData;
    }

    public void clear() {
        gameDAO.clear();
        userDAO.clear();
        authDAO.clear();
    }


}
