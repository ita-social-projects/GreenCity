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
     * Find favorite place existing by place id and user email.
     *
     * @param id        - favorite place
     * @param userEmail - user's email
     * @return FavoritePlace entity
     * @author Zakhar Skaletskyi
     */
    FavoritePlace findByIdAndUserEmail(Long id, String userEmail);
}
