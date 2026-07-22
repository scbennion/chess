package dataaccess.exceptions;

public class BadRequestException extends RuntimeException {
    public BadRequestException() {
        super("bad request");
    }
}
