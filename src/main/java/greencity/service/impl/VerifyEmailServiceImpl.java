package greencity.service.impl;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import greencity.entity.User;
import greencity.entity.VerifyEmail;
import greencity.exception.BadTokenException;
import greencity.repository.VerifyEmailRepo;
import greencity.service.VerifyEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class VerifyEmailServiceImpl implements VerifyEmailService {

    @Value("${verifyEmailTime}")
    private String expireTime;

    @Value("${address}")
    private String serverAddress;

    private VerifyEmailRepo repo;

    private JavaMailSender javaMailSender;

    @Autowired
    public VerifyEmailServiceImpl(VerifyEmailRepo repo, JavaMailSender javaMailSender) {
        this.repo = repo;
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void save(User user) throws MailException {
        log.info("VerifyEmailServiceImpl save() begin");
        VerifyEmail verifyEmail =
                VerifyEmail.builder()
                        .user(user)
                        .token(UUID.randomUUID().toString())
                        .expiryDate(calculateExpiryDate(Integer.valueOf(expireTime)))
                        .build();
        repo.save(verifyEmail);
        sentEmail(user.getEmail(), verifyEmail.getToken());
        log.info("VerifyEmailServiceImpl save() end");
    }

    @Override
    public void verify(String token) {
        log.info("VerifyEmailServiceImpl verify() begin");

        VerifyEmail verifyEmail =
                repo.findByToken(token)
                        .orElseThrow(
                                () ->
                                        new BadTokenException(
                                                "No eny email to verify by this token"));

        delete(verifyEmail);
        log.info("VerifyEmailServiceImpl verify() begin");
    }

    private Date calculateExpiryDate(Integer expiryTimeInMillisecond) {
        log.info("VerifyEmailServiceImpl calculateExpiryDate()");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MILLISECOND, expiryTimeInMillisecond);
        return new Date(cal.getTime().getTime());
    }

    private void sentEmail(String email, String token) throws MailException {
        log.info("VerifyEmailServiceImpl sentEmail() begin");
        String subject = "Registration Confirmation";
        String message = "Confirm your registration ";

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message + serverAddress + "/verifyEmail?token=" + token);
        javaMailSender.send(simpleMailMessage);
        log.info("VerifyEmailServiceImpl sentEmail() begin");
    }

    @Override
    public void delete(VerifyEmail verifyEmail) {
        log.info("VerifyEmailServiceImpl delete() begin");
        repo.delete(verifyEmail);
        log.info("VerifyEmailServiceImpl end() begin");
    }
}
