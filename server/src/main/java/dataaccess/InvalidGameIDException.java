package dataaccess;

public class InvalidGameIDException extends DataAccessException{
    public InvalidGameIDException() {
        super("bad request");
    }
}
