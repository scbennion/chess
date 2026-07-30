package dataaccess.exceptions;

public class InvalidAuthTokenException extends DataAccessException {
    public InvalidAuthTokenException() {
        super("unauthorized");
    }
}
