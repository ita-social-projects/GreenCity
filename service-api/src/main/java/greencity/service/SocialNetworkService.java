package greencity.service;

import greencity.dto.socialnetwork.SocialNetworkVO;

public interface SocialNetworkService {
    /**
     * Method for delete social network.
     *
     * @param id of {@link SocialNetworkVO}
     */
    Long delete(Long id);
}
