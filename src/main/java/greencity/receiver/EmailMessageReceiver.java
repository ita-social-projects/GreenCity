package greencity.receiver;

import greencity.message.PasswordRecoveryMessage;
import greencity.message.SendChangePlaceStatusEmailMessage;
import greencity.message.VerifyEmailMessage;
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
     * Method that is invoked on {@link VerifyEmailMessage} receiving.
     * It is responsible for sending verify email.
     */
    @RabbitListener(queues = VERIFY_EMAIL_ROUTING_QUEUE)
    public void sendVerifyEmail(VerifyEmailMessage message) {
        emailService.sendVerificationEmail(message.getId(), message.getName(), message.getEmail(), message.getToken());
    }
}

