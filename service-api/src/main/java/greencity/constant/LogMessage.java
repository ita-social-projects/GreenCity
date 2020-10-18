package greencity.constant;

public final class LogMessage {
    public static final String IN_SAVE = "in save(), entity: {}";
    public static final String IN_FIND_BY_ID = "in findById(), id: {}";
    public static final String IN_DELETE_BY_ID = "in deleteById(), id: {}";
    public static final String IN_FIND_ALL = "in findAll()";
    public static final String IN_UPDATE = "in update(), updated entity: {}";
    public static final String IN_SEND_IMMEDIATELY_REPORT = "in sendImmediatelyReport(), newPlace: {}";
    public static final String IN_SEND_DAILY_REPORT = "in sendDailyReport(), time: {}";
    public static final String IN_SEND_WEEKLY_REPORT = "in sendWeeklyReport(), time: {}";
    public static final String IN_SEND_MONTHLY_REPORT = "in sendMonthlyReport(), time: {}";
    public static final String IN_SEND_REPORT = "in sendReport(), notificationType: {}";
    public static final String IN_GET_SUBSCRIBERS = "in getSubscribers(), notificationType: {}";
    public static final String IN_GET_CATEGORIES_WITH_PLACES_MAP = "in getCategoriesWithPlacesMap(), places: {}";
    public static final String IN_GET_UNIQUE_CATEGORIES_FROM_PLACES = "in getUniqueCategoriesFromPlaces(), places: {}";

    private LogMessage() {
    }
}