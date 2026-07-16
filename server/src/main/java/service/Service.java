package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import java.util.UUID;

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
        if (input.username() == null || input.password() == null || input.email() == null)
            throw new BadRequestException();
        if (userDAO.getUser(input.username()) != null) {
           throw new AlreadyTakenException();
        }
        userDAO.createUser(input);
        AuthData authData = new AuthData(generateToken(), input.username());
        authDAO.addAuth(authData);
        return authData;
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }


}
