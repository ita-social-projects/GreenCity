package greencity.service.impl;

import greencity.constant.EmailConstant;
import greencity.entity.Place;
import greencity.entity.User;
import greencity.entity.enums.PlaceStatus;
import greencity.service.EmailService;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * {@inheritDoc}
 */
@Service
@AllArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    private JavaMailSender javaMailSender;
    private Environment env;
    private String text;

    /**
     * {@inheritDoc}
     *
     * @author Nazar Vladyka
     */
    @Override
    public void sendChangePlaceStatusNotification(Place updatable, PlaceStatus status) {
        text = "Your proposed place " + "\"<b>" + updatable.getName() + "</b>\"" + " has been <b>" + status + "</b>.";
        if (status == PlaceStatus.APPROVED) {
            text += " You can now view it in " + env.getProperty("client.address");
        }
        String finalText = text;

        sendEmail(updatable.getAuthor(), "GreenCity contributors", finalText);
    }

    private void sendEmail(User receiver, String subject, String text) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

        new Thread(() -> {
            try {
                mimeMessageHelper.setTo(receiver.getEmail());
                mimeMessageHelper.setSubject(subject);
                mimeMessage.setContent(text + EmailConstant.SIGNATURE, EmailConstant.CONTENT_TYPE);
            } catch (MessagingException e) {
                log.error(e.getMessage());
            }
        }).start();

        javaMailSender.send(mimeMessage);
    }
}
