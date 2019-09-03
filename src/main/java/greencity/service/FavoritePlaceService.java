package greencity.service;

import greencity.dto.favoriteplace.FavoritePlaceDto;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public interface FavoritePlaceService {
    /**
     * Save place as favorite for user.
     *
     * @param favoritePlaceDto - dto with favorite_place name, place id and user email
     * @param userEmail        - User email
     * @return FavoritePlaceDto instance
     * @author Zakhar Skaletskyi
     */
    FavoritePlaceDto save(FavoritePlaceDto favoritePlaceDto, String userEmail);

    /**
     * Update favorite place name for user.
     *
     * @param favoritePlaceDto - dto with favorite place name, place id and user email
     * @param userEmail        - User email
     * @return FavoritePlaceDto instance
     * @author Zakhar Skaletskyi
     */

    FavoritePlaceDto update(FavoritePlaceDto favoritePlaceDto, String userEmail);

    /**
     * Find all favorite places by user email.
     *
     * @param email - user's email
     * @return list of FavoritePlaceDto
     * @author Zakhar Skaletskyi
     */
    List<FavoritePlaceDto> findAllByUserEmail(String email);

    /**
     * Delete favorite place by place id and user email.
     *
     * @param placeId   - place id
     * @param userEmail - user's email
     * @return -  number of deleted records
     * @author Zakhar Skaletskyi
     */
    @Transactional
    int deleteByPlaceIdAndUserEmail(Long placeId, String userEmail);
}
