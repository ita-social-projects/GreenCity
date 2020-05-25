package greencity.service;

import greencity.entity.ProfilePicture;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface ProfilePictureService {
    /**
     * Method get profile picture by user id {@link ProfilePicture}.
     *
     * @return optional object {@link ProfilePicture}
     * @author Marian Datsko
     */
    Optional<ProfilePicture> getProfilePictureByUserId(Long id);
}
