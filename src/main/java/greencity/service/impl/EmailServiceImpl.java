package greencity.service.impl;

import greencity.constant.EmailConstants;
import greencity.constant.LogMessage;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.newssubscriber.NewsSubscriberRequestDto;
import greencity.entity.Category;
import greencity.entity.Place;
import greencity.entity.User;
import greencity.entity.enums.EmailNotification;
import greencity.service.EmailService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final String clientLink;
    private final String ecoNewsLink;
    private final String serverLink;

    /**
     * Constructor.
     */
    @Autowired
    public EmailServiceImpl(JavaMailSender javaMailSender,
                            ITemplateEngine templateEngine,
                            @Value("${client.address}") String clientLink,
                            @Value("${econews.address}") String ecoNewsLink,
                            @Value("${address}") String serverLink) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.clientLink = clientLink;
        this.ecoNewsLink = ecoNewsLink;
        this.serverLink = serverLink;
    }

    /**
     * {@inheritDoc}
     *
     * @author Nazar Vladyka
     */
    @Override
    public void sendChangePlaceStatusEmail(Place place) {
        log.info(LogMessage.IN_SEND_CHANGE_PLACE_STATUS_EMAIL, place);
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.CLIENT_LINK, clientLink);
        model.put(EmailConstants.USER_NAME, place.getAuthor().getFirstName());
        model.put(EmailConstants.PLACE_NAME, place.getName());
        model.put(EmailConstants.STATUS, place.getStatus().toString().toLowerCase());

        String template = createEmailTemplate(model, EmailConstants.CHANGE_PLACE_STATUS_EMAIL_PAGE);
        sendEmail(place.getAuthor(), EmailConstants.GC_CONTRIBUTORS, template);
    }

    /**
     * {@inheritDoc}
     *
     * @author Nazar Vladyka
     */
    @Override
    public void sendAddedNewPlacesReportEmail(List<User> subscribers,
                                              Map<Category, List<Place>> categoriesWithPlaces,
                                              EmailNotification notification) {
        log.info(LogMessage.IN_SEND_ADDED_NEW_PLACES_REPORT_EMAIL, subscribers, categoriesWithPlaces, notification);
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.CLIENT_LINK, clientLink);
        model.put(EmailConstants.RESULT, categoriesWithPlaces);
        model.put(EmailConstants.REPORT_TYPE, notification);

        for (User user : subscribers) {
            model.put(EmailConstants.USER_NAME, user.getFirstName());
            String template = createEmailTemplate(model, EmailConstants.NEW_PLACES_REPORT_EMAIL_PAGE);
            sendEmail(user, EmailConstants.NEW_PLACES, template);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @author Bogdan Kuzenko
     */
    @Override
    public void sendNewNewsForSubscriber(List<NewsSubscriberRequestDto> subscribers,
                                         AddEcoNewsDtoResponse newsDto) {
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.ECO_NEWS_LINK, ecoNewsLink);
        model.put(EmailConstants.NEWS_RESULT, newsDto);
        for (NewsSubscriberRequestDto dto : subscribers) {
            model.put(EmailConstants.UNSUBSCRIBE_LINK, serverLink + "/newsSubscriber?email=" + dto.getEmail());
            String template = createEmailTemplate(model, EmailConstants.NEWS_RECEIVE_EMAIL_PAGE);
            sendEmailByEmail(dto.getEmail(), EmailConstants.NEWS, template);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @author Nazar Stasyuk
     */
    @Override
    public void sendVerificationEmail(User user, String token) {
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.CLIENT_LINK, clientLink);
        model.put(EmailConstants.USER_NAME, user.getFirstName());
        model.put(EmailConstants.VERIFY_ADDRESS, serverLink + "/ownSecurity/verifyEmail?token=" + token);
        String template = createEmailTemplate(model, EmailConstants.VERIFY_EMAIL_PAGE);
        sendEmail(user, EmailConstants.VERIFY_EMAIL, template);
    }

    /**
     * {@inheritDoc}
     *
     * @author Dmytro Dovhal
     */
    @Override
    public void sendRestoreEmail(User user, String token) {
        Map<String, Object> model = new HashMap<>();
        model.put(EmailConstants.CLIENT_LINK, clientLink);
        model.put(EmailConstants.USER_NAME, user.getFirstName());
        model.put(EmailConstants.RESTORE_PASS, clientLink + "/GreenCityClient/auth/restore/" + token);
        String template = createEmailTemplate(model, EmailConstants.RESTORE_EMAIL_PAGE);
        sendEmail(user, EmailConstants.CONFIRM_RESTORING_PASS, template);
    }

    private String createEmailTemplate(Map<String, Object> vars, String templateName) {
        log.info(LogMessage.IN_CREATE_TEMPLATE_NAME, vars, templateName);
        Context context = new Context();
        context.setVariables(vars);
        return templateEngine.process("email/" + templateName, context);
    }

    private void sendEmail(User receiver, String subject, String content) {
        sendEmailByEmail(receiver.getEmail(), subject, content);
    }

    private void sendEmailByEmail(String email, String subject, String content) {
        log.info(LogMessage.IN_SEND_EMAIL, email, subject);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        try {
            //TODO set mail as a local variable greencity448@gmail.com
            mimeMessageHelper.setFrom(email);
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject(subject);
            mimeMessage.setContent(content, EmailConstants.EMAIL_CONTENT_TYPE);
        } catch (MessagingException e) {
            log.error(e.getMessage());
        }
        new Thread(() -> javaMailSender.send(mimeMessage)).start();
    }
}