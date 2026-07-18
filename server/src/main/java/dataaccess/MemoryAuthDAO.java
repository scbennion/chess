package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    private final HashMap<String, AuthData> database = new HashMap<>();

    @Override
    public AuthData createAuth(String username) {
        AuthData authData = new AuthData(generateToken(), username);
        database.put(authData.authToken(), authData);
        return authData;
    }

    @Override
    public AuthData getAuth(String authToken) {
        return database.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        database.remove(authToken);
    }

    @Override
    public void clear() {
        database.clear();
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

}
