package dataaccess.exceptions;

public class InvalidPasswordException extends DataAccessException {
    public InvalidPasswordException() {
        super("unauthorized");
    }
}
