package dataaccess;

public class InvalidAuthTokenException extends DataAccessException{
    public InvalidAuthTokenException() {
        super("unauthorized");
    }
}
