package greencity.repository;

import greencity.entity.VerifyEmail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerifyEmailRepo extends JpaRepository<VerifyEmail, Long> {
    Optional<VerifyEmail> findByToken(String token);
}
