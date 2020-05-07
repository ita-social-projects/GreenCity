package greencity.exception.exceptions;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Exception that we get when user trying to sign-up with email that already registered.
 *
 * @author Nazar Stasyuk
 */
public class UserAlreadyRegisteredException extends RuntimeException {
    private String lang;

    /**
     * Generated javadoc, must be replaced with real one.
     */
    public UserAlreadyRegisteredException(String message, String lang) {
        super(message);
        this.lang = lang;
    }


    @Override
    public String getLocalizedMessage() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("resources", new Locale(lang));
        return resourceBundle.getString("userAlreadyRegisteredErrorMessage");
    }

}
