package greencity.security.repository;

import greencity.entity.OwnSecurity;
import greencity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link OwnSecurity}.
 *
 * @author Nazar Stasyuk
 * @version 1.0
 */
@Repository
public interface OwnSecurityRepo extends JpaRepository<OwnSecurity, Long> {
    /**
     * Method for updating password.
     *
     * @param password {@link String}
     * @param id       {@link Long}
     * @author Dmytro Dovhal
     */
    @Modifying
    @Query("update OwnSecurity o set o.password = :password where o.user.id = :id")
    void updatePassword(@Param("password") String password, @Param("id") Long id);

    /**
     * Method for finding user password.
     *
     * @param user - {@link User}
     * @return
     */
    OwnSecurity findByUser(User user);
}
