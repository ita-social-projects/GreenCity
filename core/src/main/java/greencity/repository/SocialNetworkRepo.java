package greencity.repository;

import greencity.entity.SocialNetwork;
import greencity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialNetworkRepo extends JpaRepository<SocialNetwork, Long> {
    /**
     * Method deletes all {@link SocialNetwork} of certain user.
     * @param user whom to delete
     * @return amount of deleted rows
     */
    Long deleteAllByUserIs(User user);
}
