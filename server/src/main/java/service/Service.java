package service;

import chess.ChessGame.TeamColor;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;

import javax.xml.crypto.Data;

public class Service {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    public Service() {
        //to be modified when switching to SQL database
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
    }

    public AuthData register(UserData input) throws DataAccessException {
        if (input == null || input.username() == null || input.password() == null || input.email() == null) {
            throw new BadRequestException();
        } if (userDAO.getUser(input.username()) != null) {
           throw new AlreadyTakenException();
        }
        userDAO.createUser(input);
        return authDAO.createAuth(input.username());
    }

    public AuthData login(UserData loginRequest) throws DataAccessException {
        if (loginRequest.username() == null || loginRequest.password() == null || loginRequest.email() != null) {
            throw new BadRequestException();
        } UserData userData = userDAO.getUser(loginRequest.username());
        if (userData == null) {
            throw new InvalidUsernameException();
        } if (!userData.password().equals(loginRequest.password())) {
            throw new InvalidPasswordException();
        }
        return authDAO.createAuth(userData.username());
    }

    public void logout(String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            throw new InvalidAuthTokenException();
        } authDAO.deleteAuth(authToken);
    }

    public GameData[] listGames(String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            throw new InvalidAuthTokenException();
        } return gameDAO.listGames();
    }

    public int createGame(String authToken, String gameName) throws DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            throw new InvalidAuthTokenException();
        }
        GameData gameData = gameDAO.createGame(gameName);
        return gameData.gameID();
    }

    public void joinGame(String authToken, String colorString, int gameID) throws DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            throw new InvalidAuthTokenException();
        }

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

        switch(color) {
            case WHITE -> {
                if (gameData.whiteUsername() != null) {
                    throw new AlreadyTakenException();
                } else {
                    gameData = new GameData(gameData.gameID(), authData.username(), gameData.blackUsername(), gameData.gameName(), gameData.game());
                }
            } case BLACK -> {
                if (gameData.blackUsername() != null) {
                    throw new AlreadyTakenException();
                } else {
                    gameData = new GameData(gameData.gameID(), gameData.whiteUsername(), authData.username(), gameData.gameName(), gameData.game());
                }
            }
        }

        gameDAO.updateGameData(gameData);
    }

    public void clear() {
        gameDAO.clear();
        userDAO.clear();
        authDAO.clear();
    }


}
