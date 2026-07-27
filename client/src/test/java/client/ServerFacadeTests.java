package client;

import dataaccess.exceptions.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    @Test
    void clearDatabase() {
        assertDoesNotThrow(() -> serverFacade.clear());
    }

    @Test
    void registerPositive() throws DataAccessException {
        var authData = serverFacade.register(new UserData("natalie", "p", "me@email.com"));
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void registerNegative() throws Exception {
        UserData duplicate = new UserData("duplicator", "p", "me@email.com");
        var authData = serverFacade.register(duplicate);
        assertThrows(DataAccessException.class, () -> serverFacade.register(duplicate));
    }
}
