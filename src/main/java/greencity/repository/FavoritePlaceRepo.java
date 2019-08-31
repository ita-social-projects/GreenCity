package greencity.repository;

import greencity.entity.FavoritePlace;
import greencity.entity.Place;
import greencity.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoritePlaceRepo extends JpaRepository<FavoritePlace, Long> {
    /**
     * Find all favorite places by user email.
     *
     * @param email - user's email
     * @return list of favorite places
     * @author Zakhar Skaletskyi
     */
    List<FavoritePlace> findAllByUserEmail(String email);

    /**
     * Check favorite place existing by place id and user email.
     *
     * @param placeId   - place's id
     * @param userEmail - user's email
     * @return check result
     * @author Zakhar Skaletskyi
     */
    boolean existsByPlaceIdAndUserEmail(Long placeId, String userEmail);

    /**
     * Delete favorite place by place id and user email.
     *
     * @param placeId - place's id
     * @param email   - user's email
     * @author Zakhar Skaletskyi
     */
    void deleteByPlaceIdAndUserEmail(Long placeId, String email);

    /**
     * Find favorite place by place id and user id.
     *
     * @param user  - User's instance with user id
     * @param place - Place's instance with place id
     * @author Zakhar Skaletskyi
     */
    FavoritePlace findByUserAndPlace(User user, Place place);
}
