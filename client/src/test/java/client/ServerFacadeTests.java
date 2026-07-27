package client;

import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.assertTrue;

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

//    @BeforeEach
//    void clearDatabase() {
//        serverFacade.clear();
//    }

    @Test
    void register() throws Exception {
        var authData = serverFacade.register(new UserData("natalie", "p", "me@email.com"));
        assertTrue(authData.authToken().length() > 10);
    }


    @Test
    public void sampleTest() {
        assertTrue(true);
    }

}
