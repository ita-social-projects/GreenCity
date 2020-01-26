package greencity.receiver;

import static greencity.constant.ErrorMessage.ECO_NEWS_NOT_FOUND;

import greencity.constant.AppConstant;
import greencity.constant.RabbitConstants;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.exception.exceptions.NotFoundException;
import greencity.message.PasswordRecoveryMessage;
import greencity.message.SendChangePlaceStatusEmailMessage;
import greencity.repository.EcoNewsRepo;
import greencity.repository.EcoNewsTranslationRepo;
import greencity.service.EmailService;
import greencity.service.NewsSubscriberService;
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
    private final EmailService emailService;
    private final EcoNewsTranslationRepo ecoNewsTranslationRepo;
    private final EcoNewsRepo ecoNewsRepo;
    private final NewsSubscriberService newsSubscriberService;

    /**
     * Constructor with {@link EmailService} dependency declaration,
     * which is used for email sending logic.
     *
     * @param emailService service that is used for email sending logic.
     */
    public EmailMessageReceiver(EmailService emailService,
                                EcoNewsTranslationRepo ecoNewsTranslationRepo,
                                EcoNewsRepo ecoNewsRepo,
                                NewsSubscriberService newsSubscriberService) {
        this.emailService = emailService;
        this.ecoNewsTranslationRepo = ecoNewsTranslationRepo;
        this.ecoNewsRepo = ecoNewsRepo;
        this.newsSubscriberService = newsSubscriberService;
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
     *
     * @param addEcoNewsDtoResponse {@link AddEcoNewsDtoResponse} contains data needed for sending news.
     */
    @RabbitListener(queues = RabbitConstants.ADD_ECO_NEWS_QUEUE_NAME)
    public void sendNewsForSubscriber(AddEcoNewsDtoResponse addEcoNewsDtoResponse) {
        AddEcoNewsDtoResponse response = AddEcoNewsDtoResponse.builder()
            .id(addEcoNewsDtoResponse.getId())
            .title(addEcoNewsDtoResponse.getTitle())
            .imagePath(addEcoNewsDtoResponse.getImagePath())
            .text(addEcoNewsDtoResponse.getText())
            .creationDate(addEcoNewsDtoResponse.getCreationDate())
            .build();

        response.setTitle(ecoNewsTranslationRepo.findByEcoNewsAndLanguageCode(
            ecoNewsRepo.findById(addEcoNewsDtoResponse.getId())
                .orElseThrow(() -> new NotFoundException(ECO_NEWS_NOT_FOUND)),
            AppConstant.DEFAULT_LANGUAGE_CODE).getTitle());

        emailService.sendNewNewsForSubscriber(newsSubscriberService.findAll(), response);
    }
}

