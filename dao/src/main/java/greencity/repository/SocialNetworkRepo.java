package greencity.repository;

import greencity.entity.SocialNetwork;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialNetworkRepo extends JpaRepository<SocialNetwork, Long> {
}
