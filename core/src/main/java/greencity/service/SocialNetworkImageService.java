package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.socialnetwork.SocialNetworkImageResponseDTO;
import greencity.dto.socialnetwork.SocialNetworkResponseDTO;
import greencity.entity.SocialNetworkImage;
import org.springframework.data.domain.Pageable;

import java.util.List;

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

    /**
     * Find {@link SocialNetworkImage} for management by page .
     *
     * @param pageable a value with pageable configuration.
     * @return a dto of {@link PageableDto}.
     * @author Orest Mamchuk
     */
    PageableDto<SocialNetworkImageResponseDTO> findAll(Pageable pageable);

    /**
     * Method for getting User by search query.
     *
     * @param paging {@link Pageable}.
     * @param query  query to search,
     * @return PageableDto of {@link SocialNetworkResponseDTO} instances.
     */
    PageableDto<SocialNetworkImageResponseDTO> searchBy(Pageable paging, String query);

    /**
     * Method for deleting the {@link SocialNetworkImage} instance by its id.
     *
     * @param id - {@link SocialNetworkImage} instance id which will be deleted.
     */
    void delete(Long id);

    /**
     * Method deletes all {@link SocialNetworkImage} by list of ids.
     *
     * @param listId list of id {@link SocialNetworkImage}
     */
    void deleteAll(List<Long> listId);

}
