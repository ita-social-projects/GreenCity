package greencity.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import greencity.entity.User;
import greencity.entity.VerifyEmail;
import greencity.exception.BadTokenException;
import greencity.exception.UserActivationEmailTokenExpiredException;
import greencity.repository.VerifyEmailRepo;
import greencity.service.VerifyEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class VerifyEmailServiceImpl implements VerifyEmailService {

    @Value("${verifyEmailTimeHour}")
    private String expireTime;

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
                        .expiryDate(calculateExpiryDate(Integer.valueOf(expireTime)))
                        .build();
        repo.save(verifyEmail);
        sentEmail(user.getEmail(), verifyEmail.getToken());
        log.info("end");
    }

    @Override
    public void verify(String token) {
        log.info("begin");
        VerifyEmail verifyEmail =
                repo.findByToken(token)
                        .orElseThrow(
                                () ->
                                        new BadTokenException(
                                                "No eny email to verify by this token"));
        if (isDateValidate(verifyEmail.getExpiryDate())) {
            log.info("Date of user email is valid.");
            delete(verifyEmail);
        } else {
            log.info("User late with verify. Token is invalid.");
            throw new UserActivationEmailTokenExpiredException(
                    "User late with verify. Token is invalid.");
        }
        log.info("end");
    }

    @Override
    public List<VerifyEmail> findAll() {
        return repo.findAll();
    }

    private Date calculateExpiryDate(Integer expiryTimeInHour) {
        log.info("begin");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, expiryTimeInHour);
        log.info("end");
        return cal.getTime();
    }

    private void sentEmail(String email, String token) {
        log.info("begin");
        String subject = "Registration Confirmation";
        String message = "Confirm your registration ";

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message + serverAddress + "/ownSecurity/verifyEmail?token=" + token);
        javaMailSender.send(simpleMailMessage);
        log.info("end");
    }

    public boolean isDateValidate(Date emailExpiredDate) {
        return new Date().before(emailExpiredDate);
    }

    @Override
    public void delete(VerifyEmail verifyEmail) {
        log.info("begin");
        repo.delete(verifyEmail);
        log.info("end");
    }
}
