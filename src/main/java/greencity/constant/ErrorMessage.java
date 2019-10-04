package greencity.constant;

public class ErrorMessage {
    public static final String USER_NOT_FOUND_BY_ID = "The user does not exist by this id: ";
    public static final String USER_NOT_FOUND_BY_EMAIL = "The user does not exist by this email: ";
    public static final String USER_WITH_EMAIL_EXIST = "The user is already exist by this email: ";
    public static final String PLACE_NOT_FOUND_BY_ID = "The place does not exist by this id: ";
    public static final String FAVORITE_PLACE_NOT_FOUND = "The favorite place does not exist by this placeId: ";
    public static final String FAVORITE_PLACE_ALREADY_EXISTS = "Favorite place already exist for this place";
    public static final String PLACE_STATUS_NOT_DIFFERENT = "Place with id: %d already has this status: %s";
    public static final String LOCATION_NOT_FOUND_BY_ID = "The location does not exist by this id: ";
    public static final String DISCOUNT_NOT_FOUND_BY_ID = "The discount does not exist by this id: ";
    public static final String SPECIFICATION_NOT_FOUND_BY_ID = "The specification does not exist by this id: ";
    public static final String SPECIFICATION_VALUE_NOT_FOUND_BY_ID = "The specification value does not exist by this id: ";
    public static final String OPEN_HOURS_NOT_FOUND_BY_ID = "The opening hours does not exist by this id: ";
    public static final String BREAK_TIME_NOT_FOUND_BY_ID = "The opening hours does not exist by this id: ";
    public static final String CATEGORY_NOT_FOUND_BY_ID = "The category does not exist by this id: ";
    public static final String CATEGORY_NOT_FOUND_BY_NAME = "The category does not exist by this name: ";
    public static final String OPENING_HOURS_NOT_FOUND_BY_ID = "The opening hours does not exist by this id: ";
    public static final String CATEGORY_ALREADY_EXISTS_BY_THIS_NAME = "Category by this name already exists.";
    public static final String SPECIFICATION_ALREADY_EXISTS_BY_THIS_NAME = "Specification by this name already exists.";
    public static final String SPECIFICATION_VALUE_ALREADY_EXISTS_BY_THIS_NAME = "Specification value by this name already exists.";
    public static final String USER_ALREADY_REGISTERED_WITH_THIS_EMAIL = "User with this email are already registered";
    public static final String NO_ENY_USER_OWN_SECURITY_TO_DELETE = "No any ownSecurity to delete with this id: ";
    public static final String LOCATION_ALREADY_EXISTS_BY_THIS_COORDINATES = "Location by this coordinates already exists.";
    public static final String BAD_EMAIL_OR_PASSWORD = "Bad email or password";
    public static final String USER_NOT_VERIFIED = "User not verified";
    public static final String EMAIL_TOKEN_EXPIRED = "User late with verify. Token is invalid.";
    public static final String REFRESH_TOKEN_NOT_VALID = "Refresh token not valid!";
    public static final String NO_ANY_EMAIL_TO_VERIFY_BY_THIS_TOKEN = "No any email to verify by this token";
    public static final String NO_ANY_VERIFY_EMAIL_TO_DELETE = "No any VerifyEmail to delete with this id: ";
    public static final String REPEATING_VALUE_OF_WEEKDAY_VALUE = "Value of week day repeating";
    public static final String USER_DEACTIVATED = "User is deactivated";
    public static final String BAD_GOOGLE_TOKEN = "Bad google token";
    public static final String NOT_SAVE_DELETION = "This is can't be deleted";
    public static final String USER_CANT_UPDATE_HIMSELF = "User can't update yourself";
    public static final String IMPOSSIBLE_UPDATE_USER_STATUS = "Impossible to update status of admin or moderator";
    public static final String CLOSE_TIME_LATE_THAN_OPEN_TIME = "Close time have to be late than open time";
    public static final String END_TIME_LATE_THAN_START_TIME = "End time have to be late than start time";
    public static final String WRONG_BREAK_TIME = "Working hours have to contain break with right time";
    public static final String NOT_IMPLEMENTED_METHOD = "Method hasn't implementation, please create it "
        + "before use this method again";
    public static final String WRONG_DATE_TIME_FORMAT = "The date format is wrong. Should matches "
        + AppConstant.DATE_FORMAT;
    public static final String LINK_FOR_RESTORE_NOT_FOUND = "Link for sendEmailForRestore password by email not found";
    public static final String TOKEN_FOR_RESTORE_IS_INVALID = "Token is null or it doesn't exist.";
    public static final String SPECIFICATION_NOT_FOUND_BY_NAME = "The specification does not exist by this name: ";
}
