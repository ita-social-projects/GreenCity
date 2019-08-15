package greencity.repositories;

import greencity.entities.UserOwnSecurity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserOwnSecurityRepo extends JpaRepository<UserOwnSecurity, Long> {
}
