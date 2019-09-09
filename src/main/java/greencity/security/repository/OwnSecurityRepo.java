package greencity.security.repository;

import greencity.entity.OwnSecurity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for {@link OwnSecurity}.
 *
 * @author Nazar Stasyuk
 * @version 1.0
 */
public interface OwnSecurityRepo extends JpaRepository<OwnSecurity, Long> {
}
