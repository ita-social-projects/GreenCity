package greencity.security.service.impl;

import greencity.constant.ErrorMessage;
import greencity.entity.RestorePasswordEmail;
import greencity.entity.User;
import greencity.exception.NotFoundException;
import greencity.security.repository.RestorePasswordEmailRepo;
import greencity.security.service.RestorePasswordEmailService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RestorePasswordEmailServiceImpl implements RestorePasswordEmailService {
    /**
     * Time for validation email token.
     */
    @Value("${verifyEmailTimeHour}")
    private Integer expireTime;

    /**
     * This is server address. We send it to user email. And user can simply submit it.
     */
    @Value("${address}")
    private String serverAddress;

    private RestorePasswordEmailRepo repo;

    private JavaMailSender javaMailSender;


    public RestorePasswordEmailServiceImpl(RestorePasswordEmailRepo repo, JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
        this.repo = repo;
    }

    @Override
    public void save(User user) {
        RestorePasswordEmail restorePasswordEmail =
            RestorePasswordEmail.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(calculateExpiryDate(expireTime))
                .build();
        repo.save(restorePasswordEmail);

        new Thread(
            () -> {
                try {
                    sendEmail(user, restorePasswordEmail.getToken());
                } catch (MessagingException e) {
                    log.error(e.getMessage());
                }
            })
            .start();
        log.info("end");
    }

    @Override
    public void delete(RestorePasswordEmail restorePasswordEmail) {
        log.info("begin");
        if (!repo.existsById(restorePasswordEmail.getId())) {
            throw new NotFoundException(ErrorMessage.LINK_FOR_RESTORE_NOT_FOUND
                + restorePasswordEmail.getUser().getEmail());
        }
        repo.delete(restorePasswordEmail);
        log.info("end");
    }


    @Override
    public List<RestorePasswordEmail> findAll() {
        return repo.findAll();
    }

    @Override
    public boolean isDateValidate(LocalDateTime emailExpiredDate) {
        return LocalDateTime.now().isBefore(emailExpiredDate);
    }

    private void sendEmail(User user, String token) throws MessagingException {
        log.info("begin");
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        String subject = "Restore password";
        String message =
            "<b>For sendEmailForRestore your password, please follow next link.</b><br>"
                + "Hi "
                + user.getFirstName()
                + "!\n"
                + "Thanks for your interest in Green City!</b><br>";

        mimeMessageHelper.setTo(user.getEmail());
        mimeMessageHelper.setSubject(subject);
        mimeMessage.setContent(
            message + serverAddress + "/ownSecurity/changePassword?token=" + token,
            "text/html; charset=utf-8");

        javaMailSender.send(mimeMessage);
        log.info("end");
    }

    private LocalDateTime calculateExpiryDate(Integer expiryTimeInHour) {
        log.info("start");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime result = now.plusHours(expiryTimeInHour);
        log.info("finish");
        return result;
    }
}
