package greencity.repository;

import greencity.entity.SocialNetworkImage;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialNetworkImageRepo extends JpaRepository<SocialNetworkImage, Long> {
    /**
     * Method finds {@link SocialNetworkImage} by given host address.
     * @param hostPath host address
     * @return Optional of {@link SocialNetworkImage}
     */
    Optional<SocialNetworkImage> findByHostPath(String hostPath);
}
