package greencity.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.mail.MessagingException;

import greencity.entity.User;
import greencity.entity.VerifyEmail;

public interface VerifyEmailService {
    void save(User user);

    void delete(VerifyEmail verifyEmail);

    void verify(String token);

    List<VerifyEmail> findAll();

    boolean isDateValidate(LocalDateTime emailExpiredDate);
}
