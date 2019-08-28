package greencity.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import greencity.entity.User;
import greencity.entity.VerifyEmail;
import greencity.exception.BadIdException;
import greencity.exception.BadTokenException;
import greencity.exception.UserActivationEmailTokenExpiredException;
import greencity.repository.VerifyEmailRepo;
import greencity.service.VerifyEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import static greencity.constant.ErrorMessage.*;

@Service
@Slf4j
public class VerifyEmailServiceImpl implements VerifyEmailService {

    @Value("${verifyEmailTimeHour}")
    private Integer expireTime;

    @Value("${address}")
    private String serverAddress;

    private VerifyEmailRepo repo;

    private JavaMailSender javaMailSender;

    public VerifyEmailServiceImpl(VerifyEmailRepo repo, JavaMailSender javaMailSender) {
        this.repo = repo;
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void save(User user) {
        log.info("begin");
        VerifyEmail verifyEmail =
                VerifyEmail.builder()
                        .user(user)
                        .token(UUID.randomUUID().toString())
                        .expiryDate(calculateExpiryDate(expireTime))
                        .build();
        repo.save(verifyEmail);

        new Thread(
                        () -> {
                            try {
                                sentEmail(user, verifyEmail.getToken());
                            } catch (MessagingException e) {
                                log.error(e.getMessage());
                            }
                        })
                .start();
        log.info("end");
    }

    @Override
    public void verify(String token) {
        log.info("begin");
        VerifyEmail verifyEmail =
                repo.findByToken(token)
                        .orElseThrow(
                                () -> new BadTokenException(NO_ANY_EMAIL_TO_VERIFY_BY_THIS_TOKEN));
        if (isDateValidate(verifyEmail.getExpiryDate())) {
            log.info("Date of user email is valid.");
            delete(verifyEmail);
        } else {
            log.info("User late with verify. Token is invalid.");
            throw new UserActivationEmailTokenExpiredException(EMAIL_TOKEN_EXPIRED);
        }
        log.info("end");
    }

    @Override
    public List<VerifyEmail> findAll() {
        return repo.findAll();
    }

    private LocalDateTime calculateExpiryDate(Integer expiryTimeInHour) {
        log.info("begin");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime localDateTime = now.plusHours(expiryTimeInHour);
        log.info("end");
        return localDateTime;
    }

    private void sentEmail(User user, String token) throws MessagingException {
        log.info("begin");
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

        String subject = "Verify your email address";
        String message =
                "<b>Verify your email address to complete registration.</b><br>"
                        + "Hi "
                        + user.getFirstName()
                        + "!\n"
                        + "Thanks for your interest in joining Green City! To complete your registration, we need you to verify your email address. ";

        mimeMessageHelper.setTo(user.getEmail());
        mimeMessageHelper.setSubject(subject);
        mimeMessage.setContent(
                message + serverAddress + "/ownSecurity/verifyEmail?token=" + token,
                "text/html; charset=utf-8");

        javaMailSender.send(mimeMessage);
        log.info("end");
    }

    public boolean isDateValidate(LocalDateTime emailExpiredDate) {
        return LocalDateTime.now().isBefore(emailExpiredDate);
    }

    @Override
    public void delete(VerifyEmail verifyEmail) {
        log.info("begin");
        if (!repo.existsById(verifyEmail.getId())) {
            throw new BadIdException(NO_ANY_VERIFY_EMAIL_TO_DELETE + verifyEmail.getId());
        }
        repo.delete(verifyEmail);
        log.info("end");
    }
}
