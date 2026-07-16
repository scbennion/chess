package dataaccess;

public class InvalidPasswordException extends DataAccessException{
    public InvalidPasswordException() {
        super("unauthorized");
    }
}
