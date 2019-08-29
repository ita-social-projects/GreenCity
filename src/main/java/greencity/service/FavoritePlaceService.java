package greencity.service;

import greencity.dto.favoritePlace.FavoritePlaceDto;
import greencity.entity.FavoritePlace;

import java.util.List;

public interface FavoritePlaceService {
    /**
     * Save place as favorite for user
     *
     * @param favoritePlace - favorite place entity instance
     * @return FavoritePlace entity instance
     * @author Zakhar Skaletskyi
     */
    FavoritePlace save(FavoritePlace favoritePlace);
    /**
     * Save place as favorite for user
     *
     * @param favoritePlaceDto - dto for FavoritePlace entity
     * @return FavoritePlaceDto instance
     * @author Zakhar Skaletskyi
     */
    FavoritePlaceDto save(FavoritePlaceDto favoritePlaceDto);
    /**
     * Update favorite place name for user
     *
     * @param favoritePlaceDto - dto for FavoritePlace entity
     * @return FavoritePlaceDto instance
     * @author Zakhar Skaletskyi
     */
    FavoritePlaceDto update(FavoritePlaceDto favoritePlaceDto);
    /**
     * Find all favorite places by user email
     *
     * @param  email - user's email
     * @return list of dto
     * @author Zakhar Skaletskyi
     */
    List<FavoritePlaceDto> findAllByUserEmail(String email);
    /**
     * Delete favorite place by place id and user email
     *
     * @param  favoritePlaceDto - dto for FavoritePlace entity
     * @author Zakhar Skaletskyi
     */
    void deleteByPlaceIdAndUserEmail(FavoritePlaceDto favoritePlaceDto);
}
