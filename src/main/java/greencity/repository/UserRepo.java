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

    //zakhar
    boolean existsByEmail(String email);
    @Query("SELECT id from User where email=:email")
    Long findIdByEmail(String email);    //zakhar
}
