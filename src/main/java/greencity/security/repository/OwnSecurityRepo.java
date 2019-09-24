package greencity.security.repository;

import greencity.entity.OwnSecurity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository for {@link OwnSecurity}.
 *
 * @author Nazar Stasyuk
 * @version 1.0
 */
@Repository
public interface OwnSecurityRepo extends JpaRepository<OwnSecurity, Long> {
    @Modifying
    @Query("update OwnSecurity o set o.password = :password where o.user.id = :id")
    void updatePassword(@Param("password") String password, @Param("id") Long id);
}
