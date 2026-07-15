package dataaccess;

public class AlreadyTakenException extends DataAccessException {
    public AlreadyTakenException() {
        super("username already taken");
    }

}
