package greencity.constant;

public final class ErrorMessage {
    public static final String CUSTOM_GOAL_NOT_FOUND_BY_ID = "Custom goal with such id does not exist.";
    public static final String GOAL_NOT_FOUND_BY_ID = "Goal with such id does not exist ";
    public static final String GOAL_NOT_FOUND_BY_LANGUAGE_CODE = "There is no goal for such language.";
    public static final String GOAL_WRONG_ID = "Goal with this id not found : ";
    public static final String WRONG_PARAMETER = "You must choose one goal id  custom or simple. ";
    public static final String HABIT_STATISTIC_ALREADY_EXISTS = "Habit statistic already exists by such date";
    public static final String HABIT_ASSIGN_NOT_FOUND_BY_ID = "Habit assign does not exist by this id : ";
    public static final String HABIT_STATISTIC_NOT_FOUND_BY_ID = "The Habit statistic does not exist by this id : ";
    public static final String HABIT_NOT_FOUND_BY_ID = "The Habit does not exist by this id : ";
    public static final String WRONG_DATE = "Can't create habit statistic for such date";
    public static final String HABIT_TRANSLATION_NOT_FOUND = "HabitTranslation not found";
    public static final String END_TIME_LATE_THAN_START_TIME = "End time have to be late than start time";
    public static final String BREAK_TIME_NOT_FOUND_BY_ID = "The opening hours does not exist by this id: ";
    public static final String CLOSE_TIME_LATE_THAN_OPEN_TIME = "Close time have to be late than open time";
    public static final String WRONG_BREAK_TIME = "Working hours have to contain break with right time";
    public static final String OPEN_HOURS_NOT_FOUND_BY_ID = "The opening hours does not exist by this id: ";
    public static final String INVALID_LANGUAGE_CODE = "Given language code is not supported.";
    public static final String ADVICE_NOT_FOUND_BY_ID = "The name does not exist by this id: ";
    public static final String ADVICE_NOT_FOUND_BY_NAME = "The name does not exist by this name: ";
    public static final String ADVICE_NOT_UPDATED = "Advice not updated ";
    public static final String ADVICE_NOT_DELETED = "Advice not deleted ";

    public static final String CATEGORY_NOT_FOUND_BY_ID = "The category does not exist by this id: ";
    public static final String CATEGORY_NOT_FOUND_BY_NAME = "The category does not exist by this name: ";
    public static final String CATEGORY_ALREADY_EXISTS_BY_THIS_NAME = "Category by this name already exists.";
    public static final String CANNOT_ADD_PARENT_CATEGORY = "The category cannot be added to no-parent category";
    public static final String NOT_SAVE_DELETION = "This is can't be deleted";

    public static final String TAGS_NOT_FOUND = "Tips & Tricks should have at least one valid tag";
    public static final String DUPLICATED_TAG = "Tips & Tricks tags should be unique";
    public static final String INVALID_NUM_OF_TAGS =
        "Invalid tags. You must have less than " + ServiceValidationConstants.MAX_AMOUNT_OF_TAGS + " tags";
    public static final String FACT_OF_THE_DAY_NOT_FOUND = "The fact of the day not found: ";
    public static final String FACT_OF_THE_DAY_NOT_DELETED = "The fact of the day does not deleted by id: ";
    public static final String FACT_OF_THE_DAY_NOT_UPDATED = "The fact of the day does not updated by id: ";
    public static final String NEWS_SUBSCRIBER_EXIST = "Subscriber with this email address exists in the database.";
    public static final String NEWS_SUBSCRIBER_BY_EMAIL_NOT_FOUND =
        "Subscriber with this email address not found in the database.";
    public static final String INVALID_UNSUBSCRIBE_TOKEN = "Invalid unsubscribe token";
    public static final String LOCATION_NOT_FOUND_BY_ID = "The location does not exist by this id: ";
    public static final String NO_STATUS_FOR_SUCH_HABIT_ASSIGN = "There is no status for such habit for current user";
    public static final String USER_HAS_NO_STATUS_FOR_SUCH_HABIT =
        "There is no status for such habit for current user";
    public static final String HABIT_HAS_BEEN_ALREADY_ENROLLED = "You can enroll habit only once a day";
    public static final String HABIT_IS_NOT_ENROLLED = "Habit is not enrolled";
    public static final String HABIT_HAS_BEEN_ALREADY_IN_THAT_DAY = "Habit has been enrolled";
    public static final String STATUS_OF_HABIT_ASSIGN_NOT_DELETED = "Status of user habit wasn't deleted by id: ";
    public static final String STATUS_OF_HABIT_ASSIGN_NOT_UPDATED = "Status of user habit wasn't updated by id: ";
    public static final String HABIT_ASSIGN_NOT_UPDATED_BY_ID = "Habit assign does not exist by this id : ";
    public static final String HABIT_ASSIGN_NOT_FOUND_WITH_SUCH_USER_ID_AND_HABIT_ID_AND_DATE =
        "There is no habit assign for such habit, user and date";
    public static final String HABIT_ASSIGN_SUSPENDED_STATUS_NOT_UPDATED =
        "Habit assign suspended status wasn't updated because there is no habit assign with such user "
            + "and habit with id : ";
    public static final String HABIT_ASSIGN_NOT_FOUND_WITH_SUCH_USER_ID_AND_HABIT_ID =
        "There is no habit assign for such habit and user";
    public static final String USER_ALREADY_HAS_ASSIGNED_HABIT = "Current user already has assigned habit with id: ";
    public static final String USER_SUSPENDED_ASSIGNED_HABIT_FOR_CURRENT_DAY_ALREADY =
        "User already assigned and suspended this habit for today with id: ";
    public static final String HABIT_FACT_NOT_FOUND_BY_ID = "The habitfact does not exist by id: ";
    public static final String HABIT_FACT_NOT_UPDATED_BY_ID = "The habitfact does not updated by id: ";
    public static final String HABIT_FACT_NOT_DELETED_BY_ID = "The habitfact does not deleted by id: ";
    public static final String SPECIFICATION_VALUE_NOT_FOUND_BY_ID =
        "The specification value does not exist by this id: ";
    public static final String SPECIFICATION_NOT_FOUND_BY_NAME = "The specification does not exist by this name: ";
    public static final String LOCATION_IS_PRESENT = "Location is present.";
    public static final String PHOTO_IS_PRESENT = "Photo is present.";
    public static final String DISCOUNT_NOT_FOUND_BY_ID = "The discount does not exist by this id: ";
    public static final String FILE_NOT_SAVED = "File hasn't been saved";

    private ErrorMessage() {
    }
}
