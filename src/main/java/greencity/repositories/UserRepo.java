package greencity.repositories;

import greencity.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Provides an interface to manage {@link User} entity.
 */
@Repository
public interface UserRepo extends JpaRepository<User, Long> {}
