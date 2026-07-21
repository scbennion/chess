package dataaccess;

import model.AuthData;

import java.util.UUID;

public interface AuthDAO {
    AuthData createAuth(String username) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

    void clear();

    default String generateToken() {
        return UUID.randomUUID().toString();
    }

}
