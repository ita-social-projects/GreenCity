package greencity.exception;

/**
 * Error class for access place info
 * @author Dmytro Dovhal
 * @version 1.0
 * */
public class PlaceNotFoundException extends RuntimeException {
    public PlaceNotFoundException(String message) {
        super(message);
    }
}
