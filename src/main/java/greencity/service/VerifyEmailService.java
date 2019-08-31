package greencity.service;

import greencity.entity.User;
import greencity.entity.VerifyEmail;
import java.time.LocalDateTime;
import java.util.List;

public interface VerifyEmailService {
    /**
     * Generated javadoc, must be replaced with real one.
     */
    void save(User user);

    /**
     * Generated javadoc, must be replaced with real one.
     */
    void delete(VerifyEmail verifyEmail);

    /**
     * Generated javadoc, must be replaced with real one.
     */
    void verify(String token);

    /**
     * Generated javadoc, must be replaced with real one.
     */
    List<VerifyEmail> findAll();

    /**
     * Generated javadoc, must be replaced with real one.
     */
    boolean isDateValidate(LocalDateTime emailExpiredDate);
}
