package greencity.service;

import greencity.dto.favoriteplace.FavoritePlaceDto;
import greencity.dto.place.PlaceByBoundsDto;
import greencity.dto.place.PlaceInfoDto;
import greencity.entity.FavoritePlace;
import greencity.entity.Place;
import greencity.entity.User;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public interface FavoritePlaceService {
    /**
     * Save {@link Place} as {@link FavoritePlace} for user.
     *
     * @param favoritePlaceDto - dto with {@link FavoritePlace} name, {@link Place} id and {@link User} email
     * @param userEmail        - User email
     * @return {@link FavoritePlaceDto} instance
     * @author Zakhar Skaletskyi
     */
    FavoritePlaceDto save(FavoritePlaceDto favoritePlaceDto, String userEmail);

    /**
     * Update favorite place name for user.
     *
     * @param favoritePlaceDto - dto with {@link FavoritePlace} name, {@link Place} id and {@link User} email
     * @param userEmail        - {@link User} email
     * @return {@link FavoritePlaceDto} instance
     * @author Zakhar Skaletskyi
     */

    FavoritePlaceDto update(FavoritePlaceDto favoritePlaceDto, String userEmail);

    /**
     * Find all {@link FavoritePlace} by {@link User} email.
     *
     * @param email - {@link User} email
     * @return list of {@link FavoritePlaceDto}
     * @author Zakhar Skaletskyi
     */
    List<FavoritePlaceDto> findAllByUserEmail(String email);

    /**
     * Delete {@link FavoritePlace} by {@link User} email and {@link Place} id .
     *
     * @param placeId   - {@link Place} id
     * @param userEmail - {@link User} email
     * @return -  id of deleted {@link FavoritePlace}
     * @author Zakhar Skaletskyi
     */
    @Transactional
    Long deleteByUserEmailAndPlaceId(Long placeId, String userEmail);

    /**
     * FInd {@link FavoritePlace} by id.
     *
     * @param placeId - {@link Place} id
     * @return {@link FavoritePlace} entity
     * @author Zakhar Skaletskyi
     */
    FavoritePlace findByPlaceId(Long placeId);

    /**
     * Method for getting {@link FavoritePlace} as {@link Place} information.
     *
     * @param favoritePlaceId - {@link FavoritePlace} id
     * @return info about place with name from {@link FavoritePlace}
     * @author Zakhar Skaletskyi
     */
    PlaceInfoDto getInfoFavoritePlace(Long favoritePlaceId);

    /**
     * Get {@link FavoritePlace} coordinates, id and name.
     *
     * @param id    {@link FavoritePlace}
     * @param email - {@link User} email
     * @return {@link PlaceByBoundsDto} with name from favorite place
     * @author Zakhar Skaletskyi
     */
    PlaceByBoundsDto getFavoritePlaceWithLocation(Long id, String email);
}
