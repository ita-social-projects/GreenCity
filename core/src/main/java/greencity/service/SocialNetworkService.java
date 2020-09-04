package greencity.service;

import greencity.entity.SocialNetwork;
import greencity.entity.User;
import java.util.List;

/**
 * SocialNetworkService interface.
 *
 * @author Mykola Lehkyi
 */

public interface SocialNetworkService {
    /**
     * Method saves all {@link SocialNetwork}.
     * @param socialNetworkUrls socialnetwork URLs of user
     * @param user current user
     * @return list of {@link SocialNetwork}
     */
    List<SocialNetwork> saveAll(List<String> socialNetworkUrls, User user);
}
