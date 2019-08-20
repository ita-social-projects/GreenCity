package greencity.repository;

import java.util.Optional;

import greencity.entity.User;
import greencity.entity.VerifyEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VerifyEmailRepo extends JpaRepository<VerifyEmail, Long> {

    Optional<VerifyEmail> findByToken(String token);

    VerifyEmail findByUser(User user);

}
