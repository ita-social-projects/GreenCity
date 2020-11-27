package greencity.repository;

import greencity.entity.SocialNetwork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialNetworkRepo extends JpaRepository<SocialNetwork, Long> {
}
