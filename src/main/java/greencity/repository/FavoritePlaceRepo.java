package greencity.repository;

import greencity.entity.FavoritePlace;
import greencity.entity.enums.PlaceStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
     * Find favorite place existing by id and user email.
     *
     * @param id        - favorite place
     * @param userEmail - user's email
     * @return FavoritePlace entity
     * @author Zakhar Skaletskyi
     */
    FavoritePlace findByIdAndUserEmail(Long id, String userEmail);

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
     * Testsatd ftstasftsat tfsattfa stfastsad sad.
     *
     * @return a list of {@code Place}
     */
    List<FavoritePlace> findAll();
}
