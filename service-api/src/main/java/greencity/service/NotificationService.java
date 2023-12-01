package greencity.service;

import greencity.dto.place.PlaceVO;

public interface NotificationService {
    /**
     * Method for sending report about new places immediately to {@code User}'s who
     * subscribed and has {@code EmailNotification} type - IMMEDIATELY.
     *
     * @param newPlace - new {@code Place} which was added on the map
     */
    void sendImmediatelyReport(PlaceVO newPlace);

    /**
     * Method for sending report about new places at 12:00:00pm every day to
     * {@code User}'s who subscribed and has {@code EmailNotification} type - DAILY.
     */
    void sendDailyReport();

    /**
     * Method for sending report about new places at 12:00:00pm, on every Monday,
     * every month to {@code User}'s who subscribed and has
     * {@code EmailNotification} type - WEEKLY.
     */
    void sendWeeklyReport();

    /**
     * Method for sending report about new places at 12:00:00pm, on the 1st day,
     * every month to {@code User}'s who subscribed and has
     * {@code EmailNotification} type - MONTHLY.
     */
    void sendMonthlyReport();

    /**
     * Method sends general email notification.
     *
     * @param email   email to which the notification will be sent
     * @param subject subject of email message
     * @param message text of email message
     */
    void sendEmailNotification(String email, String subject, String message);
}
