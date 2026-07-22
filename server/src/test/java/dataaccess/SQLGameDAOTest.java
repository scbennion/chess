package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;

class SQLGameDAOTest extends SQLDAOTest {
    private static Server server;
    private static SQLGameDAO sqlGameDAO;
    private String gameName = "championship";

    @BeforeAll
    static void init() {
        server = new Server();
        server.run(0);
        sqlGameDAO = assertDoesNotThrow(SQLGameDAO::new);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    void createGamePositive() {
        int databaseSize = getDatabaseRows("gameTable");
        GameData gameData = makeTestGame();
        assertTrue(getDatabaseRows("gameTable") > databaseSize);
        assertNotNull(gameData.game());
        assertTrue(gameData.gameID() > 0);
    }

    @Test
    void createGameNegative() {
        GameData gameData = assertDoesNotThrow(() -> sqlGameDAO.createGame(gameName));
        assertNotEquals("not my game", gameData.gameName());
    }

    private GameData makeTestGame() {
        return assertDoesNotThrow(() -> sqlGameDAO.createGame(gameName));
    }

    private GameData makeTestGame(String name) {
        return assertDoesNotThrow(() -> sqlGameDAO.createGame(name));
    }


    @Test
    void getGamePositive() {
        GameData createdGame = makeTestGame();
        GameData retrievedGame = assertDoesNotThrow(() -> sqlGameDAO.getGame(createdGame.gameID()));
        assertNotNull(retrievedGame);
        assertTrue(getDatabaseRows("gameTable") > 0);
    }

    @Test
    void getGameNegative() {
        GameData testCreateAuth = assertDoesNotThrow(() -> makeTestGame());
        GameData testGetAuth = assertDoesNotThrow(() -> sqlGameDAO.getGame(0));
        assertNotEquals(testCreateAuth, testGetAuth);
    }


    @Test
    void listGamesPositive() {
        assertDoesNotThrow(() -> sqlGameDAO.clear());
        makeTestGame();
        makeTestGame("rematch");
        makeTestGame("comeback");
        GameData[] games = assertDoesNotThrow(() -> sqlGameDAO.listGames());
        assertEquals(3, games.length);
    }

    @Test
    void listGamesNegative() {
        assertDoesNotThrow(() -> sqlGameDAO.clear());
        makeTestGame();
        makeTestGame("final");
        GameData[] games = assertDoesNotThrow(() -> sqlGameDAO.listGames());
        assertNotEquals(games[0].gameID(), games[1].gameID());
    }

    @Test
    void updateGameData() {
    }

    @Test
    void clear() {
        assertDoesNotThrow(sqlGameDAO::clear);
        assertEquals(0, getDatabaseRows("gameTable"));
    }
}