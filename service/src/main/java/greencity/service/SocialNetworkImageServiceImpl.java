package greencity.service;

import greencity.client.UserRemoteClient;
import greencity.constant.LogMessage;
import greencity.dto.PageableDto;
import greencity.dto.socialnetwork.SocialNetworkImageResponseDTO;
import greencity.dto.socialnetwork.SocialNetworkImageRequestDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@AllArgsConstructor
@EnableCaching
public class SocialNetworkImageServiceImpl implements SocialNetworkImageService {
    private final UserRemoteClient userRemoteClient;

    /**
     * {@inheritDoc}
     *
     * @author Orest Mamchuk
     */
    @Override
    public PageableDto<SocialNetworkImageResponseDTO> findAll(Pageable pageable) {
        log.info(LogMessage.IN_FIND_ALL);

        return userRemoteClient.getAllSocialNetworkImagesRemote(pageable);
    }

    /**
     * Method for deleting SocialNetworkImage by its id.
     *
     * @param id SocialNetworkImage id instance on the user microservice which will
     *           be deleted.
     */
    @Override
    public void delete(Long id) {
        userRemoteClient.deleteSocialImage(id);
    }

    /**
     * Method for deleting all SocialNetworkImage instances by list of IDs.
     *
     * @param listId list of ids of SocialNetworkImage
     */
    @Override
    public void deleteAll(List<Long> listId) {
        userRemoteClient.deleteAllImages(listId);
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public SocialNetworkImageResponseDTO save(SocialNetworkImageRequestDTO socialNetworkImageRequestDTO,
        MultipartFile image) {
        return userRemoteClient.saveSocialImageRemote(socialNetworkImageRequestDTO, image);
    }

    /**
     * {@inheritDoc} Method for finding {@link SocialNetworkImageResponseDTO} by id
     *
     * @param id {@link SocialNetworkImageResponseDTO} instance id.
     * @return dto {@link SocialNetworkImageResponseDTO}
     */
    @Override
    public SocialNetworkImageResponseDTO findDtoById(Long id) {
        return userRemoteClient.getEcoNewsById(id);
    }

    // /**
    // * {@inheritDoc} Method for updating SocialNetworkImage
    // *
    // * @param socialNetworkImageResponseDTO - instance of
    // * {@link SocialNetworkImageResponseDTO}.
    // */
    // @Override
    // public void update(SocialNetworkImageResponseDTO
    // socialNetworkImageResponseDTO, MultipartFile image) {
    // userRemoteClient.updateSocialImage(socialNetworkImageResponseDTO, image);
    // }
}