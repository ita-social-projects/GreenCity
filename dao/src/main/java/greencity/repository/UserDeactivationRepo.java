package greencity.repository;

import greencity.entity.UserDeactivationReason;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDeactivationRepo extends JpaRepository<UserDeactivationReason, Long> {
    @Query(nativeQuery = true, value = """
            SELECT * \
            FROM reasons_for_greencity_user_deactivation \
            WHERE id_user = :id \
            ORDER BY date_of_deactivation \
            DESC LIMIT 1
        """)
    Optional<UserDeactivationReason> getLastDeactivationReason(Long id);
}
