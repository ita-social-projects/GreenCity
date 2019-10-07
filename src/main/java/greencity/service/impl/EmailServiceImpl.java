package greencity.service.impl;

import greencity.constant.LogMessage;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

/**
 * {@inheritDoc}
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    private static final String EMAIL_CONTENT_TYPE = "text/html; charset=utf-8";
    // subjects
    private static final String GC_CONTRIBUTORS = "GreenCity contributors";
    private static final String NEW_PLACES = "New places";
    private static final String VERIFY_EMAIL = "Verify your email address";
    private static final String CONFIRM_RESTORING_PASS = "Confirm restoring password";
    // params
    private static final String CLIENT_LINK = "clientLink";
    private static final String USER_NAME = "userFirstName";
    private static final String PLACE_NAME = "placeName";
    private static final String STATUS = "status";
    private static final String VERIFY_ADDRESS = "verifyAddress";
    private static final String RESTORE_PASS = "restorePassword";
    private static final String RESULT = "result";
    private static final String REPORT_TYPE = "reportType";
    // templates
    private static final String CHANGE_PLACE_STATUS_EMAIL_PAGE = "change-place-status-email-page";
    private static final String VERIFY_EMAIL_PAGE = "verify-email-page";
    private static final String RESTORE_EMAIL_PAGE = "restore-email-page";
    private static final String NEW_PLACES_REPORT_EMAIL_PAGE = "new-places-report-email-page";
    // autowired objects
    private final JavaMailSender javaMailSender;
    private final ITemplateEngine templateEngine;
    @Value("${client.address}")
    private String clientLink;
    @Value("${address}")
    private String serverLink;

    /**
     * {@inheritDoc}
     *
     * @author Nazar Vladyka
     */
    @Override
    public void sendChangePlaceStatusEmail(Place place) {
        log.info(LogMessage.IN_SEND_CHANGE_PLACE_STATUS_EMAIL, place);
        Map<String, Object> model = new HashMap<>();
        model.put(CLIENT_LINK, clientLink);
        model.put(USER_NAME, place.getAuthor().getFirstName());
        model.put(PLACE_NAME, place.getName());
        model.put(STATUS, place.getStatus().toString().toLowerCase());

        String template = createEmailTemplate(model, CHANGE_PLACE_STATUS_EMAIL_PAGE);
        sendEmail(place.getAuthor(), GC_CONTRIBUTORS, template);
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
        model.put(CLIENT_LINK, clientLink);
        model.put(RESULT, categoriesWithPlaces);
        model.put(REPORT_TYPE, notification);

        for (User user : subscribers) {
            model.put(USER_NAME, user.getFirstName());
            String template = createEmailTemplate(model, NEW_PLACES_REPORT_EMAIL_PAGE);
            sendEmail(user, NEW_PLACES, template);
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
        model.put(CLIENT_LINK, clientLink);
        model.put(USER_NAME, user.getFirstName());
        model.put(VERIFY_ADDRESS, serverLink + "/ownSecurity/verifyEmail?token=" + token);
        String template = createEmailTemplate(model, VERIFY_EMAIL_PAGE);
        sendEmail(user, VERIFY_EMAIL, template);
    }

    /**
     * {@inheritDoc}
     *
     * @author Dmytro Dovhal
     */
    @Override
    public void sendRestoreEmail(User user, String token) {
        Map<String, Object> model = new HashMap<>();
        model.put(CLIENT_LINK, clientLink);
        model.put(USER_NAME, user.getFirstName());
        model.put(RESTORE_PASS, serverLink + "/auth/restore/" + token);
        String template = createEmailTemplate(model, RESTORE_EMAIL_PAGE);
        sendEmail(user, CONFIRM_RESTORING_PASS, template);
    }

    private String createEmailTemplate(Map<String, Object> vars, String templateName) {
        log.info(LogMessage.IN_CREATE_TEMPLATE_NAME, vars, templateName);
        Context context = new Context();
        context.setVariables(vars);
        return templateEngine.process("email/" + templateName, context);
    }

    private void sendEmail(User receiver, String subject, String content) {
        log.info(LogMessage.IN_SEND_EMAIL, receiver, subject);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        try {
            mimeMessageHelper.setTo(receiver.getEmail());
            mimeMessageHelper.setSubject(subject);
            mimeMessage.setContent(content, EMAIL_CONTENT_TYPE);
        } catch (MessagingException e) {
            log.error(e.getMessage());
        }
        new Thread(() -> javaMailSender.send(mimeMessage)).start();
    }
}
