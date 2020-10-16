package greencity.service.constant;

public final class ErrorMessage {
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

    private ErrorMessage() {
    }
}
