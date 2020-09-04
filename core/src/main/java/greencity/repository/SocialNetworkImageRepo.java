package greencity.repository;

import greencity.entity.SocialNetworkImage;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialNetworkImageRepo extends JpaRepository<SocialNetworkImage, Long> {
    Optional<SocialNetworkImage> findByHostPath(String hostPath);
}
