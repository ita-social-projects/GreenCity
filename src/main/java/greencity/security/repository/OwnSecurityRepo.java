package greencity.security.repository;

import greencity.entity.OwnSecurity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnSecurityRepo extends JpaRepository<OwnSecurity, Long> {}
