package dataaccess.exceptions;

public class InvalidGameIDException extends DataAccessException {
    public InvalidGameIDException() {
        super("bad request");
    }
}
