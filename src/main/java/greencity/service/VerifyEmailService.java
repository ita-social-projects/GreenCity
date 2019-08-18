package greencity.service;

import greencity.entity.User;
import greencity.entity.VerifyEmail;
import org.springframework.mail.MailException;

public interface VerifyEmailService {
    void save(User user) throws MailException;
    void delete(VerifyEmail verifyEmail);

    void verify(String token);
}
