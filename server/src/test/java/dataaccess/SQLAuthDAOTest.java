package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.*;
import passoff.server.DatabaseTests;
import server.Server;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class SQLAuthDAOTest {
    private static Server server;
    private static SQLAuthDAO sqlAuthDAO;
    private static final String test_username = "jordan";
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
        AuthData testAuth = assertDoesNotThrow(this::createAuth);
        assertEquals(test_username, testAuth.username());
        Assertions.assertTrue(initialRowCount < getDatabaseRows(), "No new data added to database");
    }

    @Test
    void getAuthPositive() {
        AuthData testCreateAuth = assertDoesNotThrow(this::createAuth);
        AuthData testGetAuth = assertDoesNotThrow(() -> sqlAuthDAO.getAuth(testCreateAuth.authToken()));
        assertEquals(testCreateAuth, testGetAuth);
    }


    @Test
    void deleteAuth() {
    }

    @Test
    void clear() {
    }

    private AuthData createAuth() throws DataAccessException {
        return sqlAuthDAO.createAuth(test_username);
    }

    private int getDatabaseRows() {
        AtomicInteger rows = new AtomicInteger();
        executeForAllTables((tableName, connection) -> {
            try (var statement = connection.createStatement()) {
                var sql = "SELECT count(*) FROM " + tableName;
                try (var resultSet = statement.executeQuery(sql)) {
                    if (resultSet.next()) {
                        rows.addAndGet(resultSet.getInt(1));
                    }
                }
            }
        });
        return rows.get();
    }

    private void executeForAllTables(SQLAuthDAOTest.TableAction tableAction) {
        String sql = """
                    SELECT table_name
                    FROM information_schema.tables
                    WHERE table_schema = DATABASE();
                """;

        try (Connection conn = getConnection(); PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            try (var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    tableAction.execute(resultSet.getString(1), conn);
                }
            }
        } catch (ReflectiveOperationException | SQLException e) {
            Assertions.fail(e.getMessage(), e);
        }
    }

    private Connection getConnection() throws ReflectiveOperationException {
        Class<?> clazz = findDatabaseManager();
        Method getConnectionMethod = clazz.getDeclaredMethod("getConnection");
        getConnectionMethod.setAccessible(true);

        Object obj = clazz.getDeclaredConstructor().newInstance();
        return (Connection) getConnectionMethod.invoke(obj);
    }

    private Class<?> findDatabaseManager() throws ClassNotFoundException {
        if (databaseManagerClass != null) {
            return databaseManagerClass;
        }

        for (Package p : getClass().getClassLoader().getDefinedPackages()) {
            try {
                Class<?> clazz = Class.forName(p.getName() + ".DatabaseManager");
                clazz.getDeclaredMethod("getConnection");
                databaseManagerClass = clazz;
                return clazz;
            } catch (ReflectiveOperationException ignored) {
            }
        }
        throw new ClassNotFoundException("Unable to load database in order to verify persistence. " +
                "Are you using DatabaseManager to set your credentials? " +
                "Did you edit the signature of the getConnection method?");
    }

    @FunctionalInterface
    private interface TableAction {
        void execute(String tableName, Connection connection) throws SQLException;
    }

}