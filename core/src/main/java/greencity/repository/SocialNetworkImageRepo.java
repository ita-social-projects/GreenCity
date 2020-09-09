package greencity.repository;

import greencity.constant.CacheConstants;
import greencity.entity.SocialNetworkImage;
import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialNetworkImageRepo extends JpaRepository<SocialNetworkImage, Long> {
    /**
     * Method finds {@link SocialNetworkImage} by given host address.
     * @param hostPath host address
     * @return Optional of {@link SocialNetworkImage}
     */
    @Cacheable(value = CacheConstants.SOCIAL_NETWORK_IMAGE_CACHE_NAME, key = "#hostPath")
    Optional<SocialNetworkImage> findByHostPath(String hostPath);
}
