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
        serviceTest.clear();
    }

    @Test
    public void registerNegative() {
        Assertions.assertThrows(BadRequestException.class, ()->serviceTest.register(new UserData(null, null, null)));
    }

    @Test
    public void logInPositive() {
        UserData registerRequest = new UserData("bennion", "incorrect", "hi@gmail.com");
        Assertions.assertDoesNotThrow(()->serviceTest.register(registerRequest));
        UserData logInRequest = new UserData(registerRequest.username(), registerRequest.password(), null);
        Assertions.assertDoesNotThrow(()->serviceTest.login(logInRequest));
        serviceTest.clear();
    }

    @Test
    public void logInNegative() {
        Assertions.assertThrows(InvalidUsernameException.class, ()->serviceTest.login(new UserData("fake account", "1234", null)));
    }

//    @Test
//    public void logOutPositive() {
//        UserData registerRequest = new UserData("bennion", "incorrect", "hi@gmail.com");
//        Assertions.assertDoesNotThrow(()->serviceTest.register(registerRequest));
//        UserData logInRequest = new UserData(registerRequest.username(), registerRequest.password(), null);
//        Assertions.assertDoesNotThrow(()->serviceTest.login(logInRequest));
//        serviceTest.clear();
//    }

    @Test
    public void clearPositive() {
        UserData registerRequest = new UserData("usie", "pasie", "emie");
        Assertions.assertDoesNotThrow(()->serviceTest.register(registerRequest));
        serviceTest.clear();
        Assertions.assertDoesNotThrow(()->serviceTest.register(registerRequest));
    }





}
