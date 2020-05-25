package greencity.repository;

import greencity.entity.ProfilePicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface ProfilePictureRepo extends JpaRepository<ProfilePicture, Long> {
    /**
     * Method get profile picture by user id {@link ProfilePicture}.
     *
     * @return optional object {@link ProfilePicture}
     * @author Marian Datsko
     */
    @Query(value = "SELECT * FROM greencity.public.profile_picture WHERE user_id = :id", nativeQuery = true)
    Optional<ProfilePicture> getProfilePictureByUserId(Long id);
}
