package greencity.repository;

import greencity.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Provides an interface to manage {@link User} entity.
 */
@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    /**
     * Generated javadoc, must be replaced with real one.
     */
    User findByEmail(String email);

    /**
     * Find {@code User} by page.
     *
     * @param pageable pageable configuration.
     * @return {@code Page<User>}
     */
    Page<User> findAllByOrderByEmail(Pageable pageable);

    /**
     * Check user existing by email.
     *
     * @param email - User email
     * @return check result
     * @author Zakhar Skaletskyi
     */
    boolean existsByEmail(String email);

    /**
     * Find id by email.
     *
     * @param email - User email
     * @return User id
     * @author Zakhar Skaletskyi
     */
    @Query("SELECT id from User where email=:email")
    Long findIdByEmail(String email);
}
