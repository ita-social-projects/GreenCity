package greencity.repository;

import greencity.entity.UserNotificationPreference;
import greencity.enums.EmailPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Set;

public interface UserNotificationPreferenceRepo extends JpaRepository<UserNotificationPreference, Long> {
    void deleteAllByUserId(Long id);

    Set<UserNotificationPreference> findAllByUserId(Long id);

    boolean existsByUserIdAndEmailPreference(Long id, EmailPreference emailPreference);
}
