package dataaccess;

import model.AuthData;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO {

    public SQLAuthDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public AuthData createAuth(String username) {
        return null;
    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) {

    }

    @Override
    public void clear() {

    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  pet (
              `id` int NOT NULL AUTO_INCREMENT,
              `name` varchar(256) NOT NULL,
              `type` ENUM('CAT', 'DOG', 'FISH', 'FROG', 'ROCK') DEFAULT 'CAT',
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`id`),
              INDEX(type),
              INDEX(name)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };


    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage(), ex);
        }
    }
}
