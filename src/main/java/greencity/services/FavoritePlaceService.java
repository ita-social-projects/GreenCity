package greencity.services;

import greencity.entities.FavoritePlace;

public interface FavoritePlaceService {
    FavoritePlace save(FavoritePlace favoritePlace);
    FavoritePlace update(FavoritePlace favoritePlace);
    FavoritePlace findById(Long id);
    FavoritePlace findAllByUserId(FavoritePlace favoritePlace);
    void delete(FavoritePlace favoritePlace);
}
