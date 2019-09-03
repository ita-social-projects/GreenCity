package greencity.service.impl;

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

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static greencity.constant.ErrorMessage.*;

/** {@inheritDoc} */
@Service
@Slf4j
public class VerifyEmailServiceImpl implements VerifyEmailService {

    /** Time for validation email token. */
    @Value("${verifyEmailTimeHour}")
    private Integer expireTime;

    /** This is server address. We send it to user email. And user can simply submit it. */
    @Value("${address}")
    private String serverAddress;

    private VerifyEmailRepo repo;

    private JavaMailSender javaMailSender;

    /**
     * Constructor.
     *
     * @param repo {@VerifyEmailRepo} - this is repository for {@link VerifyEmail}
     * @param javaMailSender {@link JavaMailSender} - use it for sending submits to users email.
     */
    public VerifyEmailServiceImpl(VerifyEmailRepo repo, JavaMailSender javaMailSender) {
        this.repo = repo;
        this.javaMailSender = javaMailSender;
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public void verifyByToken(String token) {
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

    /** {@inheritDoc} */
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

    /**
     * Method that send email to user to verify it.
     *
     * @param user {@link User} - user that has to get submit email.
     * @param token {@link String} - token.
     */
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

    /** {@inheritDoc} */
    @Override
    public boolean isDateValidate(LocalDateTime emailExpiredDate) {
        return LocalDateTime.now().isBefore(emailExpiredDate);
    }

    /** {@inheritDoc} */
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
