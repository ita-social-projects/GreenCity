package greencity.constant;

public final class ErrorMessage {
    public static final String USER_NOT_FOUND_BY_ID = "The user does not exist by this id: ";
    public static final String USER_NOT_FOUND_BY_EMAIL = "Bad email or password: ";
    public static final String USER_WITH_EMAIL_EXIST = "The user already exists by this email: ";
    public static final String PLACE_NOT_FOUND_BY_ID = "The place does not exist by this id: ";
    public static final String FAVORITE_PLACE_NOT_FOUND = "The favorite place does not exist ";
    public static final String FAVORITE_PLACE_ALREADY_EXISTS =
            "Favorite place already exist for this placeId: %d and user with email: %s";
    public static final String PLACE_STATUS_NOT_DIFFERENT = "Place with id: %d already has this status: %s";
    public static final String LOCATION_NOT_FOUND_BY_ID = "The location does not exist by this id: ";
    public static final String DISCOUNT_NOT_FOUND_BY_ID = "The discount does not exist by this id: ";
    public static final String SPECIFICATION_NOT_FOUND_BY_ID = "The specification does not exist by this id: ";
    public static final String SPECIFICATION_VALUE_NOT_FOUND_BY_ID =
            "The specification value does not exist by this id: ";
    public static final String OPEN_HOURS_NOT_FOUND_BY_ID = "The opening hours does not exist by this id: ";
    public static final String BREAK_TIME_NOT_FOUND_BY_ID = "The opening hours does not exist by this id: ";
    public static final String CATEGORY_NOT_FOUND_BY_ID = "The category does not exist by this id: ";
    public static final String CATEGORY_NOT_FOUND_BY_NAME = "The category does not exist by this name: ";
    public static final String OPENING_HOURS_NOT_FOUND_BY_ID = "The opening hours does not exist by this id: ";
    public static final String CATEGORY_ALREADY_EXISTS_BY_THIS_NAME = "Category by this name already exists.";
    public static final String SPECIFICATION_ALREADY_EXISTS_BY_THIS_NAME = "Specification by this name already exists.";
    public static final String SPECIFICATION_VALUE_ALREADY_EXISTS_BY_THIS_NAME =
            "Specification value by this name already exists.";
    public static final String USER_ALREADY_REGISTERED_WITH_THIS_EMAIL = "User with this email is already registered";
    public static final String NO_ANY_OWN_SECURITY_TO_DELETE = "No any ownSecurity to delete with this id: ";
    public static final String LOCATION_ALREADY_EXISTS_BY_THIS_COORDINATES =
            "Location by this coordinates already exists.";
    public static final String BAD_EMAIL = "Bad email";
    public static final String BAD_PASSWORD = "Bad password";
    public static final String USER_NOT_VERIFIED = "User not verified";
    public static final String EMAIL_TOKEN_EXPIRED = "User late with verify. Token is invalid.";
    public static final String REFRESH_TOKEN_NOT_VALID = "Refresh token not valid!";
    public static final String NO_ANY_EMAIL_TO_VERIFY_BY_THIS_TOKEN = "No any email to verify by this token";
    public static final String NO_ANY_VERIFY_EMAIL_TO_DELETE = "No any VerifyEmail to delete with this id: ";
    public static final String REPEATING_VALUE_OF_WEEKDAY_VALUE = "Value of week day repeating";
    public static final String USER_DEACTIVATED = "User is deactivated";
    public static final String BAD_GOOGLE_TOKEN = "Bad google token";
    public static final String BAD_FACEBOOK_TOKEN = "Bad facebook token";
    public static final String NOT_SAVE_DELETION = "This is can't be deleted";
    public static final String USER_CANT_UPDATE_HIMSELF = "User can't update yourself";
    public static final String IMPOSSIBLE_UPDATE_USER_STATUS = "Impossible to update status of admin or moderator";
    public static final String CLOSE_TIME_LATE_THAN_OPEN_TIME = "Close time have to be late than open time";
    public static final String END_TIME_LATE_THAN_START_TIME = "End time have to be late than start time";
    public static final String WRONG_BREAK_TIME = "Working hours have to contain break with right time";
    public static final String NOT_IMPLEMENTED_METHOD =
            "Method hasn't implementation, please create it before use this method again";
    public static final String WRONG_DATE_TIME_FORMAT =
            "The date format is wrong. Should matches " + AppConstant.DATE_FORMAT;
    public static final String LINK_FOR_RESTORE_NOT_FOUND = "Link for sendEmailForRestore password by email not found";
    public static final String TOKEN_FOR_RESTORE_IS_INVALID = "Token is null or it doesn't exist.";
    public static final String SPECIFICATION_NOT_FOUND_BY_NAME = "The specification does not exist by this name: ";
    public static final String USER_HAS_BLOCKED_STATUS = "User has blocked status.";
    public static final String PHOTO_IS_PRESENT = "Photo is present.";
    public static final String LOCATION_IS_PRESENT = "Location is present.";
    public static final String COMMENT_NOT_FOUND_EXCEPTION = "The comment with entered id doesn't exist";
    public static final String PASSWORD_DOES_NOT_MATCH = "The password doesn't match";
    public static final String PASSWORDS_DO_NOT_MATCHES = "The passwords don't matches";
    public static final String PASSWORD_RESTORE_LINK_ALREADY_SENT =
            "Password restore link already sent, please check your email: ";
    public static final String HABIT_EXIST = "The Habit is already exist by this date:  ";
    public static final String HABIT_NOT_FOUND_BY_ID = "The Habit does not exist by this id : ";
    public static final String WRONG_DATE = "Can't create habit statistic for such date";
    public static final String HABIT_STATISTIC_NOT_FOUND_BY_ID = "The Habit statistic does not exist by this id : ";
    public static final String USER_HAS_NO_GOALS = "This user hasn't selected any goals yet";
    public static final String USER_HAS_NO_AVAILABLE_GOALS = "This user is tracking all available goals";
    public static final String USER_HAS_NO_AVAILABLE_HABIT_DICTIONARY =
            "This user is tracking all available habit dictionary";
    public static final String USER_HAS_NO_AVAILABLE_CUSTOM_GOALS = "This user is tracking all available custom goals";
    public static final String USER_GOAL_WHERE_NOT_SAVED = "This UserGoal(s) already exist(s): ";
    public static final String USER_GOAL_NOT_FOUND = "UserGoal(s) with this id not found: ";
    public static final String USER_GOAL_STATUS_NOT_UPDATED = "User goal status was not updated";
    public static final String CUSTOM_GOAL_WHERE_NOT_SAVED = "This CustomGoal(s) already exist(s): ";
    public static final String USER_HAS_NO_SUCH_GOAL = "This user has no goal with id: ";
    public static final String NOT_A_CURRENT_USER = "You can't perform actions with the data of other user";
    public static final String INVALID_HABIT_ID = "Invalid habit id ";
    public static final String INVALID_HABIT_NAME = "Invalid habit name ";
    public static final String ADVICE_NOT_FOUND_BY_ID = "The name does not exist by this id: ";
    public static final String HABIT_FACT_NOT_FOUND_BY_ID = "The fact does not exist by id: ";
    public static final String FACT_OF_DAY_NOT_FOUND_BY_LANGUAGE_ID = "The fact of day does not exist by language id: ";
    public static final String HABIT_FACT_NOT_SAVED_BY_NAME = "The fact does not saved by name: ";
    public static final String HABIT_FACT_NOT_UPDATED_BY_ID = "The fact does not updated by id: ";
    public static final String HABIT_FACT_NOT_DELETED_BY_ID = "The fact does not deleted by id: ";
    public static final String ADVICE_NOT_FOUND_BY_NAME = "The name does not exist by this name: ";
    public static final String ADVICE_NOT_SAVED_BY_NAME = "The name with such name already exist: ";
    public static final String ADVICE_NOT_UPDATED = "Advice not updated ";
    public static final String ADVICE_NOT_DELETED = "Advice not deleted ";
    public static final String USER_HAS_NOT_ANY_HABITS = "There is no habits for such user.";
    public static final String USER_HAS_NOT_HABITS_WITH_SUCH_STATUS =
            "There is no active habits for such user with such status ";
    public static final String USER_GOAL_STATUS_IS_ALREADY_DONE = "This UserGoal is already done ";
    public static final String HABIT_STATISTIC_ALREADY_EXISTS = "Habit statistic already exists by such date";
    public static final String GOAL_NOT_FOUND_BY_ID = "Goal with such id does not exist ";
    public static final String HABIT_IS_SAVED = "This habit is saved to user";
    public static final String HABIT_NOT_FOUND_BY_USER_ID_AND_HABIT_DICTIONARY_ID =
            "Habit with such userId and habitDictionaryId does not exist";
    public static final String DELETE_LIST_ID_CANNOT_BE_EMPTY = "List with id cann`t be empty.";
    public static final String NOT_DELETE_LAST_HABIT = "Can`t delete last habit.";
    public static final String CUSTOM_GOAL_FOR_THIS_USER_ALREADY_EXIST =
            "Custom goal with same text for this user already exist.";
    public static final String CUSTOM_GOAL_NOT_FOUND_BY_ID = "Custom goal with such id does not exist.";
    public static final String CUSTOM_GOAL_NOT_FOUND = "The user doesn't have any custom goal.";
    public static final String GOAL_NOT_FOUND_BY_LANGUAGE_CODE = "There is no goal for such language.";
    public static final String HABIT_DICTIONARY_TRANSLATION_NOT_FOUND = "Don`t found HabitDictionaryTranslation";
    public static final String NEWS_SUBSCRIBER_EXIST = "Subscriber with this email address exists in the database.";
    public static final String NEWS_SUBSCRIBER_BY_EMAIL_NOT_FOUND =
            "Subscriber with this email address not found in the database.";
    public static final String NEWS_SUBSCRIBERS_NOT_FOUND = "Not found any subscriber in the database.";
    public static final String NEWS_SUBSCRIBER_NOT_DELETED = "The subscriber was not delete.";
    public static final String NEWS_SUBSCRIBER_NOT_SAVED = "The subscriber was not saved.";
    public static final String ECO_NEWS_NOT_SAVED = "Eco news haven't been saved because of constraint violation";
    public static final String FILE_NOT_SAVED = "File hasn't been saved";
    public static final String ECO_NEWS_NOT_FOUND = "Eco news haven't been found";
    public static final String ECO_NEWS_NOT_FOUND_BY_ID = "Eco news doesn't exist by this id: ";
    public static final String ECO_NEWS_NOT_DELETED = "Eco news hasn't been deleted";
    public static final String INVALID_UNSUBSCRIBE_TOKEN = "Invalid unsubscribe token";
    public static final String CAN_NOT_CREATE_EVENT_INSTANCE =
        "Can not create instance of events, used constructor that differ from events superclass.";
    public static final String INVALID_LANGUAGE_CODE = "Given language code is not supported.";
    public static final String TAG_NOT_FOUND = "The tag does not exist by this name: ";
    public static final String TAGS_NOT_FOUND = "Tips & Tricks should have at least one valid tag";
    public static final String INVALID_NUM_OF_TAGS =
        "Invalid tags. You must have less than " + ValidationConstants.MAX_AMOUNT_OF_TAGS + " tags";
    public static final String DUPLICATED_TAG = "Tips & Tricks tags should be unique";
    public static final String TIPS_AND_TRICKS_NOT_FOUND_BY_ID = "Tips & tricks advice doesn't exist by this id: ";
    public static final String TIPS_AND_TRICKS_NOT_SAVED =
        "Tips & tricks advice hasn't been saved due to constraint violation";
    public static final String INVALID_URI = "The string could not be parsed as a URI reference.";
    public static final String MALFORMED_URL = "Malformed URL. The string could not be parsed.";
    public static final String PROFILE_PICTURE_NOT_FOUND_BY_ID = "Profile picture not found by id : ";
    public static final String WRONG_COUNT_OF_TAGS_EXCEPTION =
        "Count of tags should be at least one but not more three";
    public static final String NOT_FOUND_ANY_FRIENDS = "Not found any friends by id: ";
    public static final String FRIEND_EXISTS = "Friend with this id has already been added : ";
    public static final String CANNOT_REPLY_THE_REPLY = "Can not make a reply to a reply";
    public static final String USER_HAS_NO_PERMISSION = "Current user has no permission for this action";
    public static final String CANNOT_REPLY_WITH_OTHER_DIFFERENT_TIPSANDTRICKS_ID = "Can not make a reply with " +
        "different TipsAndTricks Id";

    private ErrorMessage() {
    }
}
