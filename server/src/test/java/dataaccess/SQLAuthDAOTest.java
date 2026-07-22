package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.*;
import server.Server;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class SQLAuthDAOTest extends SQLDAOTest {
    private static Server server;
    private static SQLAuthDAO sqlAuthDAO;
    private static final String test_username = "john";
    private static Class<?> databaseManagerClass;

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
        int initialRowCount = getDatabaseRows();
        AuthData testAuth = assertDoesNotThrow(() -> createAuth());
        assertEquals(test_username, testAuth.username());
        Assertions.assertTrue(initialRowCount < getDatabaseRows(), "No new data added to database");
    }

    @Test
    void createAuthNegative() {
        AuthData testAuth = assertDoesNotThrow(() -> createAuth("bad actor"));
        assertNotEquals(test_username, testAuth.username());
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
        int initialRowCount = getDatabaseRows();
        assertDoesNotThrow(() -> sqlAuthDAO.deleteAuth(testCreateAuth.authToken()));
        Assertions.assertTrue(initialRowCount > getDatabaseRows(), "No data deleted from database");
    }

    @Test
    void deleteAuthNegative() {
        int initialRowCount = getDatabaseRows();
        assertDoesNotThrow(() -> sqlAuthDAO.deleteAuth(""));
        Assertions.assertFalse(initialRowCount > getDatabaseRows(), "No data deleted from database");
    }

    @Test
    void clear() {
        assertDoesNotThrow(() -> sqlAuthDAO.clear());
        assertEquals(0, getDatabaseRows());
    }

    private AuthData createAuth() throws DataAccessException {
        return sqlAuthDAO.createAuth(test_username);
    }

    private AuthData createAuth(String username) throws DataAccessException {
        return sqlAuthDAO.createAuth(username);
    }
}