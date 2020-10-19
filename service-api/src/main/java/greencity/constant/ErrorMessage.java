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

    private ErrorMessage() {
    }
}
