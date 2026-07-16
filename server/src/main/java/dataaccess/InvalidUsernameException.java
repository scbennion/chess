package dataaccess;

public class InvalidUsernameException extends DataAccessException {
    public InvalidUsernameException() {
        super("unauthorized");
    }
}
