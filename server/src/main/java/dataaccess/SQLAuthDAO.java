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
    public AuthData createAuth(String username) throws DataAccessException {
        String authToken = generateToken();
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("INSERT INTO authTable (authToken, username) VALUES (?,?);")) {
                preparedStatement.setString(1, authToken);
                preparedStatement.setString(2, username);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage(), ex);
        }
        return new AuthData(authToken, username);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String username = null;
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT authToken, username FROM authTable WHERE authToken=?")) {
                preparedStatement.setString(1, authToken);
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        username = rs.getString("username");
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage(), ex);
        }
        return new AuthData(authToken, username);
    }

    @Override
    public void deleteAuth(String authToken) {

    }

    @Override
    public void clear() {

    }

    private void updateDatabase(String[] statements) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : statements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage(), ex);
        }
    }

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS  authTable (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`)
            );
            """
        };
        updateDatabase(createStatements);
    }
}
