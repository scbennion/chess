package dataaccess.exceptions;

public class InvalidUsernameException extends DataAccessException {
    public InvalidUsernameException() {
        super("unauthorized");
    }
}
