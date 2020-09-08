package greencity.service;

import greencity.entity.SocialNetworkImage;

/**
 * SocialNetworkImageService interface.
 *
 * @author Mykola Lehkyi
 */
public interface SocialNetworkImageService {
    /**
     * Method creates or returns existed {@link SocialNetworkImage} by given url.
     * @param url a well-formed url
     * @return {@link SocialNetworkImage}
     */
    SocialNetworkImage getSocialNetworkImageByUrl(String url);
}
