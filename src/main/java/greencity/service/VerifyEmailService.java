package greencity.service;

import java.util.Date;
import java.util.List;

import greencity.entity.User;
import greencity.entity.VerifyEmail;

public interface VerifyEmailService {
    void save(User user);

    void delete(VerifyEmail verifyEmail);

    void verify(String token);

    List<VerifyEmail> findAll();

    boolean isDateValidate(Date emailExpiredDate);
}
