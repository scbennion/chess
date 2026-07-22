package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;

class SQLAuthDAOTest extends SQLDAOTest {
    private static Server server;
    private static SQLAuthDAO sqlAuthDAO;
    private static final String TEST_USERNAME = "john";

    @BeforeAll
    static void init() {
        server = new Server();
        server.run(0);
        sqlAuthDAO = assertDoesNotThrow(SQLAuthDAO::new);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    void createAuthPositive() {
        AuthData testAuth = assertDoesNotThrow(() -> createAuth());
        assertEquals(TEST_USERNAME, testAuth.username());
    }

    @Test
    void createAuthNegative() {
        AuthData testAuth = assertDoesNotThrow(() -> createAuth("bad actor"));
        assertNotEquals(TEST_USERNAME, testAuth.username());
    }


    @Test
    void getAuthPositive() {
        AuthData testCreateAuth = assertDoesNotThrow(() -> createAuth());
        AuthData testGetAuth = assertDoesNotThrow(() -> sqlAuthDAO.getAuth(testCreateAuth.authToken()));
        assertEquals(testCreateAuth, testGetAuth);
    }

    @Test
    void getAuthNegative() {
        AuthData testCreateAuth = assertDoesNotThrow(() -> createAuth());
        AuthData testGetAuth = assertDoesNotThrow(() -> sqlAuthDAO.getAuth(""));
        assertNotEquals(testCreateAuth, testGetAuth);
    }

    @Test
    void deleteAuthPositive() {
        AuthData testCreateAuth = assertDoesNotThrow(() -> createAuth());
        int initialRowCount = getDatabaseRows("authTable");
        assertDoesNotThrow(() -> sqlAuthDAO.deleteAuth(testCreateAuth.authToken()));
        Assertions.assertTrue(initialRowCount > getDatabaseRows("authTable"), "No data deleted from database");
    }

    @Test
    void deleteAuthNegative() {
        int initialRowCount = getDatabaseRows("authTable");
        assertDoesNotThrow(() -> sqlAuthDAO.deleteAuth(""));
        Assertions.assertFalse(initialRowCount > getDatabaseRows("authTable"), "No data deleted from database");
    }

    @Test
    void clear() {
        assertDoesNotThrow(() -> sqlAuthDAO.clear());
        assertEquals(0, getDatabaseRows("authTable"));
    }

    private AuthData createAuth() throws DataAccessException {
        return sqlAuthDAO.createAuth(TEST_USERNAME);
    }

    private AuthData createAuth(String username) throws DataAccessException {
        return sqlAuthDAO.createAuth(username);
    }
}