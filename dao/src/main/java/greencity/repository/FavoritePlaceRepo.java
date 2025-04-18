package greencity.repository;

import greencity.entity.FavoritePlace;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
     * Find favorite place existing by place id and user email.
     *
     * @param id        - favorite place
     * @param userEmail - user's email
     * @return FavoritePlace entity
     * @author Zakhar Skaletskyi
     */
    FavoritePlace findByPlaceIdAndUserEmail(Long id, String userEmail);

    /**
     * Find favorite place by place id.
     *
     * @param placeId - favorite place
     * @return FavoritePlace entity
     * @author Zakhar Skaletskyi
     */
    FavoritePlace findByPlaceId(Long placeId);

    /**
     * Find all favorite places locations ids by user email.
     *
     * @param email - user's email
     * @return list of favorite places locations ids
     * @author Olena Sotnik
     */
    @Query("SELECT fp.place.location.id FROM FavoritePlace AS fp "
        + "WHERE fp.user = "
        + "(SELECT u FROM User AS u WHERE u.email = :email)")
    List<Long> findAllFavoritePlaceLocationIdsByUserEmail(String email);
}
