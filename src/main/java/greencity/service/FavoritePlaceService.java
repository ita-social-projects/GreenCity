package greencity.service;

import greencity.dto.favoriteplace.FavoritePlaceDto;
import java.util.List;

public interface FavoritePlaceService {
    /**
     * Save place as favorite for user.
     *
     * @param favoritePlaceDto - dto with favorite_place name, place id and user email
     * @return FavoritePlaceDto instance
     * @author Zakhar Skaletskyi
     */
    FavoritePlaceDto save(FavoritePlaceDto favoritePlaceDto);

    /**
     * Update favorite place name for user.
     *
     * @param favoritePlaceDto - dto with favorite place name, place id and user email
     * @return FavoritePlaceDto instance
     * @author Zakhar Skaletskyi
     */
    FavoritePlaceDto update(FavoritePlaceDto favoritePlaceDto);

    /**
     * Find all favorite places by user email.
     *
     * @param email - user's email
     * @return list of dto
     * @author Zakhar Skaletskyi
     */
    List<FavoritePlaceDto> findAllByUserEmail(String email);

    /**
     * Delete favorite place by place id and user email.
     *
     * @param favoritePlaceDto - dto with favorite place name, place id and user email
     * @author Zakhar Skaletskyi
     */
    void deleteByPlaceIdAndUserEmail(FavoritePlaceDto favoritePlaceDto);
}
