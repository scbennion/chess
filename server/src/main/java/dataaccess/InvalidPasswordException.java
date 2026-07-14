package dataaccess;

public class InvalidPasswordException extends DataAccessException{
    public InvalidPasswordException(String message) {
        super(message);
    }
}
