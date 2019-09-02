package greencity.repository;

import greencity.entity.UserOwnSecurity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserOwnSecurityRepo extends JpaRepository<UserOwnSecurity, Long> {
}
