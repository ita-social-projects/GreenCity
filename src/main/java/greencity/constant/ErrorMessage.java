package greencity.constant;

public class ErrorMessage {

    public static final String USER_NOT_FOUND_BY_ID = "The user does not exist by this id: ";
    public static final String USER_ALREADY_REGISTERED_WITH_THIS_EMAIL =
            "User with this email are already registered";
    public static final String NO_ENY_USER_OWN_SECURITY_TO_DELETE =
            "No any userOwnSecurity to delete with this id: ";
    public static final String BAD_EMAIL_OR_PASSWORD = "Bad email or password";
    public static final String EMAIL_TOKEN_EXPIRED = "User late with verify. Token is invalid.";
    public static final String REFRESH_TOKEN_NOT_VALID = "Refresh token not valid!";
    public static final String NO_ANY_EMAIL_TO_VERIFY_BY_THIS_TOKEN =
            "No eny email to verify by this token";
    public static final String NO_ANY_VERIFY_EMAIL_TO_DELETE =
            "No any VerifyEmail to delete with this id: ";
}
