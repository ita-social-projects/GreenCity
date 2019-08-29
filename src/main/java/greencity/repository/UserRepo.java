package greencity.repository;

import greencity.entity.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/** Provides an interface to manage {@link User} entity. */
public interface UserRepo extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    /**
     * Find {@code User} by page.
     *
     * @param pageable pageable configuration.
     * @return {@code Page<User>}
     */
    Page<User> findAllByOrderByEmail(Pageable pageable);
}
