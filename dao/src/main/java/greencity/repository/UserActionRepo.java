package greencity.repository;

import greencity.entity.Achievement;
import greencity.entity.UserAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActionRepo extends JpaRepository<UserAction, Long> {
    /**
     * Method find {@link UserAction} by id.
     *
     * @param id of {@link UserAction}
     * @return UserAction {@link UserAction}
     * @author Orest Mamchuk
     */
    UserAction findByUserId(Long id);
}
