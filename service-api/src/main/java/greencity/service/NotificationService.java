package greencity.service;

import greencity.dto.place.PlaceVO;
import greencity.message.GeneralEmailMessage;
import greencity.message.HabitAssignNotificationMessage;
import greencity.message.UserReceivedCommentMessage;
import greencity.message.UserReceivedCommentReplyMessage;
import greencity.message.UserTaggedInCommentMessage;
import java.util.Set;

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
     * method sends a general email notification to many Users.
     *
     * @param usersEmails {@link Set} to this users email will be sent.
     * @param subject     subject of email message.
     * @param message     text of email message.
     * @author Yurii Midianyi
     */
    void sendEmailNotification(Set<String> usersEmails, String subject, String message);

    /**
     * method sends a general email notification to one User.
     *
     * @param generalEmailMessage {@link GeneralEmailMessage}.
     * @author Yurii Midianyi
     */
    void sendEmailNotification(GeneralEmailMessage generalEmailMessage);

    /**
     * Method send a habit notification message to user.
     *
     * @param message {@link HabitAssignNotificationMessage}.
     */
    void sendHabitAssignEmailNotification(HabitAssignNotificationMessage message);

    /**
     * Method send a notification message when user is mentioned in comment.
     *
     * @param message {@link UserTaggedInCommentMessage}.
     */
    void sendUsersTaggedInCommentEmailNotification(UserTaggedInCommentMessage message);

    /**
     * Method send a notification message when user received comment.
     *
     * @param message {@link UserReceivedCommentMessage}.
     */
    void sendUserReceivedCommentEmailNotification(UserReceivedCommentMessage message);

    /**
     * Method send a notification message when user received reply to the comment.
     *
     * @param message {@link UserReceivedCommentReplyMessage}.
     */
    void sendUserReceivedCommentReplyEmailNotification(UserReceivedCommentReplyMessage message);

    public void sendEmailNotificationLikes(GeneralEmailMessage generalEmailMessage);

    public void sendEmailNotificationComments(GeneralEmailMessage generalEmailMessage);

    public void sendEmailNotificationInvites(GeneralEmailMessage generalEmailMessage);
}
