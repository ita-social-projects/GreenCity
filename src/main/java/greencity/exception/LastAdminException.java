package greencity.exception;

/**
 * Exception that we get when we want update last admin.
 *
 * @author Rostyslav Khasanov
 */
public class LastAdminException extends RuntimeException {
    /**
     * Constructor for CountOfAdminsException.
     *
     * @param message - giving message.
     */
    public LastAdminException(String message) {
        super(message);
    }
}
