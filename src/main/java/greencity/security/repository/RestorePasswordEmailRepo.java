package greencity.security.repository;

import greencity.entity.RestorePasswordEmail;
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
     * @author Dmytro Dovhal
     */
    Optional<RestorePasswordEmail> findByToken(String token);
}
