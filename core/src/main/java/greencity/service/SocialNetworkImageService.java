package greencity.service;

import greencity.entity.SocialNetworkImage;

/**
 * SocialNetworkImageService interface.
 *
 * @author Mykola Lehkyi
 */
public interface SocialNetworkImageService {
    SocialNetworkImage getSocialNetworkImageByUrl(String url);
}
