package greencity.constant;

public class EmailConstants {
    public static final String EMAIL_CONTENT_TYPE = "text/html; charset=utf-8";
    // subjects
    public static final String GC_CONTRIBUTORS = "GreenCity contributors";
    public static final String NEW_PLACES = "New places";
    public static final String VERIFY_EMAIL = "Verify your email address";
    public static final String CONFIRM_RESTORING_PASS = "Confirm restoring password";
    // params
    public static final String CLIENT_LINK = "clientLink";
    public static final String USER_NAME = "userFirstName";
    public static final String PLACE_NAME = "placeName";
    public static final String STATUS = "status";
    public static final String VERIFY_ADDRESS = "verifyAddress";
    public static final String RESTORE_PASS = "restorePassword";
    public static final String RESULT = "result";
    public static final String REPORT_TYPE = "reportType";
    // templates
    public static final String CHANGE_PLACE_STATUS_EMAIL_PAGE = "change-place-status-email-page";
    public static final String VERIFY_EMAIL_PAGE = "verify-email-page";
    public static final String RESTORE_EMAIL_PAGE = "restore-email-page";
    public static final String NEW_PLACES_REPORT_EMAIL_PAGE = "new-places-report-email-page";

    private EmailConstants() {
    }
}
