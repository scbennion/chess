package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

public class SQLUserDAO implements UserDAO {
    @Override
    public void clear() {

    }

    @Override
    public void createUser(UserData userData) {

    }

    @Override
    public UserData getUser(String username) {
        return null;
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private boolean checkHashPassword(String password) {
        String hashedString = "find string in database";
        return BCrypt.checkpw(password, hashedString);
    }
}
