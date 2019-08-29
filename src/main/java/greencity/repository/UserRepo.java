package greencity.repository;

import greencity.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
/** Provides an interface to manage {@link User} entity. */
public interface UserRepo extends JpaRepository<User, Long> {
    User findByEmail(String email);

    /**
     * Find {@code User} by page.
     *
     * @param pageable pageable configuration.
     * @return {@code Page<User>}
     */
    Page<User> findAllByOrderByEmail(Pageable pageable);

    /**
     * Check user existing by email
     *
     * @param email - User email
     * @return check result
     * @author Zakhar Skaletskyi
     */
    boolean existsByEmail(String email);

    @Query("SELECT id from User where email=:email")
    /**
     * Find id by email
     *
     * @param email- User email
     * @return User id
     * @author Zakhar Skaletskyi
     */
    Long findIdByEmail(String email);
}
