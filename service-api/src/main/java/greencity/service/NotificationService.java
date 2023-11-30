package greencity.service;

import greencity.client.RestClient;
import greencity.dto.place.PlaceVO;
import greencity.message.AbstractEmailMessage;
import greencity.message.NewsCommentMessage;

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

    void sendEmailNotification(String email, String name, String subject, String message);
}
