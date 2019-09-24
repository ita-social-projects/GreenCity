package greencity.security.repository;

import greencity.entity.RestorePasswordEmail;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestorePasswordEmailRepo extends JpaRepository<RestorePasswordEmail, Long> {
    /**
     * Method that allow you find {@link RestorePasswordEmail} by token.
     *
     * @param token - {@link String}
     * @return {@link Optional}
     */
    Optional<RestorePasswordEmail> findByToken(String token);
}
