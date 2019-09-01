package greencity.repository;

import greencity.entity.UserOwnSecurity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for {@link UserOwnSecurity}
 *
 * @author Nazar Stasyuk
 * @version 1.0
 */
public interface UserOwnSecurityRepo extends JpaRepository<UserOwnSecurity, Long> {}
