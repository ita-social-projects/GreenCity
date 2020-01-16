package greencity.event.listeners;

import static greencity.constant.ErrorMessage.ECO_NEWS_NOT_FOUND;

import greencity.constant.AppConstant;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.event.SendNewsEvent;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.EcoNewsRepo;
import greencity.repository.EcoNewsTranslationRepo;
import greencity.service.EmailService;
import greencity.service.NewsSubscriberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Class, that contains methods for listening event from {@link greencity.service.EcoNewsService}.
 */
@Component
public class SendNewsEventListener implements ApplicationListener<SendNewsEvent> {
    private final EcoNewsTranslationRepo ecoNewsTranslationRepo;
    private final EcoNewsRepo ecoNewsRepo;
    private final EmailService emailService;
    private final NewsSubscriberService newsSubscriberService;

    /**
     * All args constructor.
     */
    @Autowired
    public SendNewsEventListener(EcoNewsTranslationRepo ecoNewsTranslationRepo,
                                 EcoNewsRepo ecoNewsRepo,
                                 EmailService emailService,
                                 NewsSubscriberService newsSubscriberService) {
        this.ecoNewsTranslationRepo = ecoNewsTranslationRepo;
        this.ecoNewsRepo = ecoNewsRepo;
        this.emailService = emailService;
        this.newsSubscriberService = newsSubscriberService;
    }

    /**
     * Method, that listen to {@link SendNewsEvent} and after triggering sends news for users who
     * subscribed for updates.
     *
     * @param event {@link SendNewsEvent} with body, that contains data needed for sending news.
     */
    @Override
    public void onApplicationEvent(SendNewsEvent event) {
        AddEcoNewsDtoResponse response = AddEcoNewsDtoResponse.builder()
            .id(event.getBody().getId())
            .title(event.getBody().getTitle())
            .imagePath(event.getBody().getImagePath())
            .text(event.getBody().getText())
            .creationDate(event.getBody().getCreationDate())
            .build();

        response.setTitle(ecoNewsTranslationRepo.findByEcoNewsAndLanguageCode(
            ecoNewsRepo.findById(event.getBody().getId()).orElseThrow(() -> new NotFoundException(ECO_NEWS_NOT_FOUND)),
            AppConstant.DEFAULT_LANGUAGE_CODE).getTitle());

        emailService.sendNewNewsForSubscriber(newsSubscriberService.findAll(), response);
    }
}
