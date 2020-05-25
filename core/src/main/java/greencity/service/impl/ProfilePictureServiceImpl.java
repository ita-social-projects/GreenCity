package greencity.service.impl;

import greencity.entity.ProfilePicture;
import greencity.repository.ProfilePictureRepo;
import greencity.service.ProfilePictureService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ProfilePictureServiceImpl implements ProfilePictureService {
    private final ProfilePictureRepo profilePictureRepo;

    /**
     * {@inheritDoc}
     *
     * @author Datsko Marian
     */
    @Override
    public Optional<ProfilePicture> getProfilePictureByUserId(Long id) {
        return profilePictureRepo.getProfilePictureByUserId(id);
    }
}
