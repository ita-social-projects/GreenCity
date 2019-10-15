package greencity.security.repository;

import greencity.entity.RestorePasswordEmail;
import greencity.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestorePasswordEmailRepo extends JpaRepository<RestorePasswordEmail, Long> {
    /**
     * Method that allow you find {@link RestorePasswordEmail} by token.
     *
     * @param token - {@link String}
     * @return {@link Optional}
     */
    Optional<RestorePasswordEmail> findByToken(String token);

    /**
     * Method for finding by user.
     *
     * @param user - {@link User} - user for search
     * @return - {@link RestorePasswordEmail}
     */
    RestorePasswordEmail findByUser(User user);
}
