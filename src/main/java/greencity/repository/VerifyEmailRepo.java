package greencity.repository;

import greencity.entity.User;
import greencity.entity.VerifyEmail;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerifyEmailRepo extends JpaRepository<VerifyEmail, Long> {
    /**
     * Generated javadoc, must be replaced with real one.
     */
    Optional<VerifyEmail> findByToken(String token);

    /**
     * Generated javadoc, must be replaced with real one.
     */
    VerifyEmail findByUser(User user);
}
