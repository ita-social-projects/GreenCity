package greencity.security.service;

import greencity.entity.RestorePasswordEmail;
import greencity.entity.User;
import java.time.LocalDateTime;
import java.util.List;

public interface RestorePasswordEmailService {
    /**
     * Save method.
     *
     * @param user {@link User} - we use here user, who previously have been found by email.
     */
    void save(User user);

    /**
     * Method that provide delete {@link RestorePasswordEmail}.
     *
     * @param restorePasswordEmail {@link RestorePasswordEmail}
     */
    void delete(RestorePasswordEmail restorePasswordEmail);

    /**
     * Find all method.
     *
     * @return {@link List}
     */
    List<RestorePasswordEmail> findAll();

    /**
     * Method that check if user not late with restoring of his email.
     *
     * @return {@code boolean}
     */
    boolean isDateValidate(LocalDateTime emailExpiredDate);
}
