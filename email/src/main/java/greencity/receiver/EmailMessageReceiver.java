package greencity.receiver;

import greencity.message.*;
import greencity.service.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ message receiver that is used for listening to email sending-related
 * queues.
 */
@Component
public class EmailMessageReceiver {
    private static final String PASSWORD_RECOVERY_QUEUE = "password-recovery-queue";
    public static final String CHANGE_PLACE_STATUS_QUEUE = "change-place-status";
    public static final String VERIFY_EMAIL_ROUTING_QUEUE = "verify-email-queue";
    private static final String ADD_ECO_NEWS_QUEUE_NAME = "eco_news_queue";
    public static final String SEND_REPORT_QUEUE = "send-report";
    public static final String SEND_HABIT_NOTIFICATION = "send-habit-notification-queue";
    private final EmailService emailService;

    /**
     * Constructor with {@link EmailService} dependency declaration,
     * which is used for email sending logic.
     *
     * @param emailService service that is used for email sending logic.
     */
    public EmailMessageReceiver(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Method that is invoked on {@link PasswordRecoveryMessage} receiving.
     * It is responsible for sending password recovery emails.
     */
    @RabbitListener(queues = PASSWORD_RECOVERY_QUEUE)
    public void sendPasswordRecoveryEmail(PasswordRecoveryMessage message) {
        emailService.sendRestoreEmail(
            message.getUserId(),
            message.getUserFirstName(),
            message.getUserEmail(),
            message.getRecoveryToken()
        );
    }

    /**
     * Method that is invoked on {@link SendChangePlaceStatusEmailMessage} receiving.
     * It is responsible for sending change place status emails.
     */
    @RabbitListener(queues = CHANGE_PLACE_STATUS_QUEUE)
    public void sendChangePlaceStatusEmail(SendChangePlaceStatusEmailMessage message) {
        emailService.sendChangePlaceStatusEmail(message.getAuthorFirstName(),
            message.getPlaceName(), message.getPlaceStatus(), message.getAuthorEmail());
    }

    /**
     * Method, that listen to adding eco news and after triggering sends news for users who
     * subscribed for updates.
     */
    @RabbitListener(queues = ADD_ECO_NEWS_QUEUE_NAME)
    public void sendNewsForSubscriber(AddEcoNewsMessage addEcoNewsMessage) {
        emailService.sendNewNewsForSubscriber(addEcoNewsMessage.getSubscribers(),
            addEcoNewsMessage.getAddEcoNewsDtoResponse());
    }

    /**
     * Method that is invoked on {@link VerifyEmailMessage} receiving.
     * It is responsible for sending verify email.
     */
    @RabbitListener(queues = VERIFY_EMAIL_ROUTING_QUEUE)
    public void sendVerifyEmail(VerifyEmailMessage message) {
        emailService.sendVerificationEmail(message.getId(), message.getName(), message.getEmail(), message.getToken());
    }

    /**
     * Method that is invoked on {@link SendReportEmailMessage} receiving.
     * It is responsible for sending report emails.
     */
    @RabbitListener(queues = SEND_REPORT_QUEUE)
    public void sendReportEmail(SendReportEmailMessage message) {
        emailService.sendAddedNewPlacesReportEmail(message.getSubscribers(),
            message.getCategoriesDtoWithPlacesDtoMap(), message.getEmailNotification());
    }

    /**
     * Method that is invoked on {@link SendHabitNotification} receiving.
     * It is responsible for sending notification letters about not marking habits.
     */
    @RabbitListener(queues = SEND_HABIT_NOTIFICATION)
    public void sendHabitNotification(SendHabitNotification sendHabitNotification) {
        emailService.sendHabitNotification(sendHabitNotification.getName(), sendHabitNotification.getEmail());
    }
}