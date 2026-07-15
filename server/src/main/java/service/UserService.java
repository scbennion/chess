package service;

import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

public class UserService {
    private UserDAO userDAO;

    public UserService() {
        userDAO = new MemoryUserDAO();
    }


    public AuthData register(UserData input) {
        System.out.println("registering");
        boolean userAlreadyExists  = userDAO.getUser(input.username()) != null;
        System.out.printf("User exists? %s", userAlreadyExists ? "yes": "no");
        userDAO.createUser(input);
        return null;
    }

}
