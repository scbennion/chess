package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;

class SQLAuthDAOTest {
    private static Server server;
    private static SQLAuthDAO sqlAuthDAO;

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
    void createAuth() {
        String test_username = "john green";
        AuthData testAuth = assertDoesNotThrow(() -> sqlAuthDAO.createAuth(test_username));
        assertEquals(test_username, testAuth.username());
    }
}