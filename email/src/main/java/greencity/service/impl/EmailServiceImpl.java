package greencity.service.impl;

import greencity.constant.EmailConstants;
import greencity.constant.LogMessage;
import greencity.dto.category.CategoryDto;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.newssubscriber.NewsSubscriberResponseDto;
import greencity.dto.place.PlaceNotificationDto;
import greencity.dto.user.PlaceAuthorDto;
import greencity.service.EmailService;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

/**
 * {@inheritDoc}
 */
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    private final ITemplateEngine templateEngine;
    private final Executor executor;
    private final String clientLink;
    private final String ecoNewsLink;
    private final String serverLink;
    private final String senderEmailAddress;

    /**
     * Constructor.
     */
    @Autowired
    public EmailServiceImpl(JavaMailSender javaMailSender,
                            ITemplateEngine templateEngine,
                            @Qualifier("sendEmailExecutor") Executor executor,
                            @Value("${client.address}") String clientLink,
                            @Value("${econews.address}") String ecoNewsLink,
                            @Value("${address}") String serverLink,
                            @Value("${sender.email.address}") String senderEmailAddress) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.executor = executor;
        this.clientLink = clientLink;
        this.ecoNewsLink = ecoNewsLink;
        this.serverLink = serverLink;
        this.senderEmailAddress = senderEmailAddress;
    }

    /**
     * {@inheritDoc}
     *
     * @author Nazar Vladyka && Yurii Olkhovskyi
     */
    @Override
    public void sendChangePlaceStatusEmail(String authorName, String placeName,
                                           String placeStatus, String authorEmail) {
        log.info(LogMessage.IN_SEND_CHANGE_PLACE_STATUS_EMAIL, placeName);
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.CLIENT_LINK, clientLink);
        model.put(EmailConstants.USER_NAME, authorName);
        model.put(EmailConstants.PLACE_NAME, placeName);
        model.put(EmailConstants.STATUS, placeStatus);

        String template = createEmailTemplate(model, EmailConstants.CHANGE_PLACE_STATUS_EMAIL_PAGE);
        sendEmail(authorEmail, EmailConstants.GC_CONTRIBUTORS, template);
    }

    /**
     * {@inheritDoc}
     *
     * @author Nazar Vladyka && Kuzenko Bogdan
     */
    @Override
    public void sendAddedNewPlacesReportEmail(List<PlaceAuthorDto> subscribers,
                                              Map<CategoryDto, List<PlaceNotificationDto>> categoriesWithPlaces,
                                              String notification) {
        log.info(LogMessage.IN_SEND_ADDED_NEW_PLACES_REPORT_EMAIL, null, null, notification);
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.CLIENT_LINK, clientLink);
        model.put(EmailConstants.RESULT, categoriesWithPlaces);
        model.put(EmailConstants.REPORT_TYPE, notification);

        for (PlaceAuthorDto user : subscribers) {
            model.put(EmailConstants.USER_NAME, user.getName());
            String template = createEmailTemplate(model, EmailConstants.NEW_PLACES_REPORT_EMAIL_PAGE);
            sendEmail(user.getEmail(), EmailConstants.NEW_PLACES, template);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @author Bogdan Kuzenko
     */
    @Override
    public void sendNewNewsForSubscriber(List<NewsSubscriberResponseDto> subscribers,
                                         AddEcoNewsDtoResponse newsDto) {
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.ECO_NEWS_LINK, ecoNewsLink);
        model.put(EmailConstants.NEWS_RESULT, newsDto);
        for (NewsSubscriberResponseDto dto : subscribers) {
            try {
                model.put(EmailConstants.UNSUBSCRIBE_LINK, serverLink + "/newsSubscriber/unsubscribe?email="
                    + URLEncoder.encode(dto.getEmail(), StandardCharsets.UTF_8.toString())
                    + "&unsubscribeToken=" + dto.getUnsubscribeToken());
            } catch (UnsupportedEncodingException e) {
                log.error(e.getMessage());
            }
            String template = createEmailTemplate(model, EmailConstants.NEWS_RECEIVE_EMAIL_PAGE);
            sendEmail(dto.getEmail(), EmailConstants.NEWS, template);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @author Volodymyr Turko
     */
    @Override
    public void sendVerificationEmail(Long id, String name, String email, String token) {
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.CLIENT_LINK, clientLink);
        model.put(EmailConstants.USER_NAME, name);
        model.put(EmailConstants.VERIFY_ADDRESS, serverLink + "/ownSecurity/verifyEmail?token="
            + token + "&user_id=" + id);
        String template = createEmailTemplate(model, EmailConstants.VERIFY_EMAIL_PAGE);
        sendEmail(email, EmailConstants.VERIFY_EMAIL, template);
    }

    /**
     * Sends password recovery email using separated user parameters.
     *
     * @param userId       the user id is used for recovery link building.
     * @param userFistName user first name is used in email model constants.
     * @param userEmail    user email which will be used for sending recovery letter.
     * @param token        password recovery token.
     */
    @Override
    public void sendRestoreEmail(Long userId, String userName, String userEmail, String token) {
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.CLIENT_LINK, clientLink);
        model.put(EmailConstants.USER_NAME, userName);
        model.put(EmailConstants.RESTORE_PASS, clientLink + "/#/auth/restore?" + "token=" + token
            + "&user_id=" + userId);
        String template = createEmailTemplate(model, EmailConstants.RESTORE_EMAIL_PAGE);
        sendEmail(userEmail, EmailConstants.CONFIRM_RESTORING_PASS, template);
    }

    private String createEmailTemplate(Map<String, Object> vars, String templateName) {
        log.info(LogMessage.IN_CREATE_TEMPLATE_NAME, null, templateName);
        Context context = new Context();
        context.setVariables(vars);
        return templateEngine.process("email/" + templateName, context);
    }

    private void sendEmail(String receiverEmail, String subject, String content) {
        log.info(LogMessage.IN_SEND_EMAIL, receiverEmail, subject);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        try {
            mimeMessageHelper.setFrom(senderEmailAddress);
            mimeMessageHelper.setTo(receiverEmail);
            mimeMessageHelper.setSubject(subject);
            mimeMessage.setContent(content, EmailConstants.EMAIL_CONTENT_TYPE);
        } catch (MessagingException e) {
            log.error(e.getMessage());
        }
        executor.execute(() -> javaMailSender.send(mimeMessage));
    }
}