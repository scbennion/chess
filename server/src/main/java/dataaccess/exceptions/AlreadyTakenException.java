package dataaccess.exceptions;

public class AlreadyTakenException extends DataAccessException {
    public AlreadyTakenException() {
        super("already taken");
    }

}
