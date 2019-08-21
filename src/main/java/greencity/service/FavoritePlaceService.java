package greencity.service;

import greencity.entity.FavoritePlace;

import java.util.List;

public interface FavoritePlaceService {
    FavoritePlace save(FavoritePlace favoritePlace);

    FavoritePlace update(FavoritePlace favoritePlace);

    FavoritePlace findById(Long id);

     List<FavoritePlace> findAllByUserId(Long id) ;

    void deleteById(long id);
}
