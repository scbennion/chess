package dataaccess;

import model.UserData;
import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{
    private HashMap<String, UserData> database = new HashMap<>();

    @Override
    public void clear() {
        database.clear();
    }

    @Override
    public void createUser(UserData userData) {
        database.put(userData.username(), userData);
    }

    @Override
    public UserData getUser(String username) {
        return database.get(username);
    }
}
