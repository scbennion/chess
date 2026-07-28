package client;

import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.InvalidAuthTokenException;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    private final UserData TEST_USER = new UserData("popcorn", "pop", "pop@gmail.com");
    private final UserData TEST_USER_2 = new UserData("brown", "Kentucky", "jbrown@gmail.com");
    private final String TEST_GAME_NAME = "Rookie Game";

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
        var authData = serverFacade.register(TEST_USER);
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void registerNegative() throws Exception {
        var authData = serverFacade.register(TEST_USER_2);
        assertThrows(DataAccessException.class, () -> serverFacade.register(TEST_USER_2));
    }

    @Test
    void loginPositive() {
        assertDoesNotThrow(() -> serverFacade.register(TEST_USER));
        var loginRequest = new UserData(TEST_USER.username(), TEST_USER.password(), null);
        var authData = assertDoesNotThrow(() -> serverFacade.login(loginRequest));
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void loginNegative() {
        assertThrows(DataAccessException.class, () -> serverFacade.login(TEST_USER));
    }

    @Test
    void logoutPositive() {
        var authData = assertDoesNotThrow(() -> serverFacade.register(TEST_USER));
        assertDoesNotThrow(() -> serverFacade.logout(authData.authToken()));
        var loginRequest = new UserData(TEST_USER.username(), TEST_USER.password(), null);
        assertDoesNotThrow(() -> serverFacade.login(loginRequest));
    }

    @Test
    void logoutNegative() {
        assertThrows(DataAccessException.class, () -> serverFacade.logout("fake token"));
    }

    @Test
    void createPositive() {
        var authData = assertDoesNotThrow(() -> serverFacade.register(TEST_USER));
        int gameID = assertDoesNotThrow(() -> serverFacade.createGame(authData.authToken(), TEST_GAME_NAME));
        assertTrue(gameID > 0);
    }

    @Test
    void createNegative() {
        assertThrows(DataAccessException.class, () -> serverFacade.createGame("BAD TOKEN", TEST_GAME_NAME));
    }

    @Test
    void listPositive() {
        var authData = assertDoesNotThrow(() -> serverFacade.register(TEST_USER));
        int gameID1 = assertDoesNotThrow(() -> serverFacade.createGame(authData.authToken(), "titled tuesday"));
        int gameID2 = assertDoesNotThrow(() -> serverFacade.createGame(authData.authToken(), "bobby fischer"));
        GameData[] games = assertDoesNotThrow(() -> serverFacade.listGames(authData.authToken()));
        assert (games.length == 2);
        assert (games[0].gameID() == gameID1 || games[0].gameID() == gameID2);
        assert (games[1].gameID() == gameID1 || games[1].gameID() == gameID2);
    }

    @Test
    void listNegative() {
        assertThrows(DataAccessException.class, () -> serverFacade.listGames("fake auth token"));
    }

    @Test
    void joinPositive() {
        var authToken1 = assertDoesNotThrow(() -> serverFacade.register(TEST_USER)).authToken();
        var authToken2 = assertDoesNotThrow(() -> serverFacade.register(TEST_USER_2)).authToken();
        int gameID = assertDoesNotThrow(() -> serverFacade.createGame(authToken1, "lichess exhibition"));
        assertDoesNotThrow(() -> serverFacade.joinGame(authToken1, "WHITE", gameID));
        assertDoesNotThrow(() -> serverFacade.joinGame(authToken2, "BLACK", gameID));
        GameData[] games = assertDoesNotThrow(() -> serverFacade.listGames(authToken1));
        assert (games[0].whiteUsername().equals(TEST_USER.username()));
        assert (games[0].blackUsername().equals(TEST_USER_2.username()));
    }

    @Test
    void joinNegative() {
        var authToken = assertDoesNotThrow(() -> serverFacade.register(TEST_USER)).authToken();
        int gameID = assertDoesNotThrow(() -> serverFacade.createGame(authToken, "first game"));
        assertThrows(DataAccessException.class, () -> serverFacade.joinGame(authToken, "bad color", gameID));
    }

}
