package dataaccess;

import dataaccess.exceptions.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;

class SQLUserDAOTest extends SQLDAOTest {
    private static Server server;
    private static SQLUserDAO sqlUserDAO;
    private static final UserData TEST_USER = new UserData("fabiano", "caruana", "fc@class.com");

    @BeforeAll
    static void init() {
        server = new Server();
        server.run(0);
        sqlUserDAO = assertDoesNotThrow(SQLUserDAO::new);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    void clear() {
        assertDoesNotThrow(() -> sqlUserDAO.clear());
        assertEquals(0, getDatabaseRows("userTable"));
    }

    private void createUser(UserData userData) throws DataAccessException {
        sqlUserDAO.createUser(userData);
    }

    @Test
    void createUserPositive() {
        assertDoesNotThrow(this::clear);
        assertDoesNotThrow(() -> createUser(TEST_USER));
    }

    @Test
    void createUserNegative() {
        assertDoesNotThrow(this::clear);
        assertThrows(DataAccessException.class, () -> createUser(new UserData(null, null, null)));
    }


    @Test
    void getUserPositive() {
        assertDoesNotThrow(this::clear);
        assertDoesNotThrow(() -> createUser(TEST_USER));
        try {
            assertTrue(sqlUserDAO.passwordsMatch(sqlUserDAO.getUser(TEST_USER.username()).password(), TEST_USER.password()));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getUserNegative() {
        assertDoesNotThrow(this::clear);
        assertDoesNotThrow(() -> createUser(TEST_USER));
        try {
            assertNull(sqlUserDAO.getUser("fake username"));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}