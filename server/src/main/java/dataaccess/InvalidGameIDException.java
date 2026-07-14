package dataaccess;

public class InvalidGameIDException extends DataAccessException{
    public InvalidGameIDException(String message) {
        super(message);
    }
}
