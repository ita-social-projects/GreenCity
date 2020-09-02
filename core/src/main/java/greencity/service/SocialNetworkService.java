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
    List<SocialNetwork> saveAll(List<String> socialNetworkUrls, User user);
}
