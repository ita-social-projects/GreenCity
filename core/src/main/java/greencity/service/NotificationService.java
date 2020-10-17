package greencity.service;

import greencity.entity.Place;
import greencity.entity.User;
import greencity.enums.EmailNotification;

public interface NotificationService {
    /**
     * Method for sending report about new places immediately to {@link User}'s who subscribed
     * and has {@link EmailNotification} type - IMMEDIATELY.
     *
     * @param newPlace - new {@link Place} which was added on the map
     */
    void sendImmediatelyReport(Place newPlace);

    /**
     * Method for sending report about new places at 12:00:00pm every day to {@link User}'s who subscribed
     * and has {@link EmailNotification} type - DAILY.
     */
    void sendDailyReport();

    /**
     * Method for sending report about new places at 12:00:00pm, on every Monday, every month to {@link User}'s
     * who subscribed and has {@link EmailNotification} type - WEEKLY.
     */
    void sendWeeklyReport();

    /**
     * Method for sending report about new places at 12:00:00pm, on the 1st day, every month to {@link User}'s
     * who subscribed and has {@link EmailNotification} type - MONTHLY.
     */
    void sendMonthlyReport();
}
