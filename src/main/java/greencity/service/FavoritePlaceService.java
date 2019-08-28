package greencity.service;

import greencity.dto.favoritePlace.FavoritePlaceDto;
import greencity.dto.favoritePlace.FavoritePlaceUpdateDto;
import greencity.entity.FavoritePlace;

import java.util.List;

public interface FavoritePlaceService {
    FavoritePlace save(FavoritePlace favoritePlace);

    FavoritePlaceDto save(FavoritePlaceDto favoritePlaceDto);

    FavoritePlaceUpdateDto update(FavoritePlaceUpdateDto favoritePlaceUpdateDto);

    FavoritePlace findById(Long id);

    //void deleteById(long id);

    List<FavoritePlaceDto> findAllByUserEmail(String email);

    //    @Override //not used?
    //    @Override //not used?
//    public FavoritePlaceDto findByPlaceIdAndUserId(Long id) {
//        log.info("In findById() method.");
//        return repo.findById(id).orElseThrow(() -> new NotFoundException("No place with this id:" + id));
//
//    }
//
    //
    void deleteByPlaceIdAndUserEmail(FavoritePlaceDto favoritePlaceDto);
}
