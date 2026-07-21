package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ServiceTests {
    private final Service serviceTest = new Service();

    @Test
    public void registerPositive() {
        AuthData authData;
        try {
            authData = serviceTest.register(new UserData("username", "password", "email"));
        } catch (DataAccessException e) {
            authData = null;
        }
        Assertions.assertNotNull(authData);
        Assertions.assertEquals("username", authData.username());
        Assertions.assertDoesNotThrow(serviceTest::clear);
    }

    @Test
    public void registerNegative() {
        Assertions.assertThrows(BadRequestException.class, () -> serviceTest.register(new UserData(null, null, null)));
    }

    @Test
    public void logInPositive() {
        UserData registerRequest = new UserData("bennion", "incorrect", "hi@gmail.com");
        Assertions.assertDoesNotThrow(() -> serviceTest.register(registerRequest));
        UserData logInRequest = new UserData(registerRequest.username(), registerRequest.password(), null);
        Assertions.assertDoesNotThrow(() -> serviceTest.login(logInRequest));
        Assertions.assertDoesNotThrow(serviceTest::clear);
    }

    @Test
    public void logInNegative() {
        Assertions.assertThrows(InvalidUsernameException.class, () -> serviceTest.login(new UserData("fake account", "1234", null)));
    }

    @Test
    public void logOutPositive() {
        UserData registerRequest = new UserData("bonbon", "incorrect", "hi@gmail.com");
        Assertions.assertDoesNotThrow(() -> serviceTest.register(registerRequest));
        UserData logInRequest = new UserData(registerRequest.username(), registerRequest.password(), null);
        AuthData authData = Assertions.assertDoesNotThrow(() -> serviceTest.login(logInRequest));
        Assertions.assertDoesNotThrow(() -> serviceTest.logout(authData.authToken()));
        Assertions.assertDoesNotThrow(serviceTest::clear);
    }

    @Test
    public void logOutNegative() {
        Assertions.assertThrows(InvalidAuthTokenException.class, () -> serviceTest.logout("bad token"));
        Assertions.assertDoesNotThrow(serviceTest::clear);
    }

    @Test
    public void listPositive() {
        UserData registerRequest = new UserData("tester", "incorrect", "hi@gmail.com");
        AuthData authData = Assertions.assertDoesNotThrow(() -> serviceTest.register(registerRequest));
        Assertions.assertDoesNotThrow(() -> serviceTest.createGame(authData.authToken(), "my game"));
        Assertions.assertDoesNotThrow(() -> serviceTest.listGames(authData.authToken()));
        Assertions.assertDoesNotThrow(serviceTest::clear);
    }

    @Test
    public void listNegative() {
        Assertions.assertThrows(InvalidAuthTokenException.class, () -> serviceTest.listGames("bad token"));
    }


    @Test
    public void clearPositive() {
        UserData registerRequest = new UserData("usie", "pasie", "emie");
        Assertions.assertDoesNotThrow(() -> serviceTest.register(registerRequest));
        Assertions.assertDoesNotThrow(serviceTest::clear);
        Assertions.assertDoesNotThrow(() -> serviceTest.register(registerRequest));
        Assertions.assertDoesNotThrow(serviceTest::clear);
    }

    @Test
    public void createGamePositive() {
        UserData registerRequest = new UserData("testing", "incorrect", "hi@gmail.com");
        AuthData authData = Assertions.assertDoesNotThrow(() -> serviceTest.register(registerRequest));
        Assertions.assertDoesNotThrow(() -> serviceTest.createGame(authData.authToken(), "my game"));
        Assertions.assertDoesNotThrow(serviceTest::clear);
    }

    @Test
    public void createGameNegative() {
        Assertions.assertThrows(InvalidAuthTokenException.class, () -> serviceTest.createGame("bad token", "my game"));
        Assertions.assertDoesNotThrow(serviceTest::clear);
    }

    @Test
    public void joinGamePositive() {
        UserData registerRequest = new UserData("testing", "incorrect", "hi@gmail.com");
        AuthData authData = Assertions.assertDoesNotThrow(() -> serviceTest.register(registerRequest));
        int gameID = Assertions.assertDoesNotThrow(() -> serviceTest.createGame(authData.authToken(), "my game"));
        Assertions.assertDoesNotThrow(() -> serviceTest.joinGame(authData.authToken(), "WHITE", gameID));
        Assertions.assertDoesNotThrow(serviceTest::clear);
    }

    @Test
    public void joinGameNegative() {
        Assertions.assertThrows(InvalidAuthTokenException.class, () -> serviceTest.joinGame("bad token", "WHITE", 1));
        Assertions.assertDoesNotThrow(serviceTest::clear);
    }
}
