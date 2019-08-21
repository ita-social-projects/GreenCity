package greencity.services.impl;

import greencity.entities.FavoritePlace;
import greencity.repositories.FavoritePlaceRepo;
import greencity.services.FavoritePlaceService;
import greencity.exceptions.BadIdException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class FavoritePlaceServiceImpl implements FavoritePlaceService {
    private FavoritePlaceRepo repo;

    @Override
    public FavoritePlace save(FavoritePlace favoritePlace) {
        log.info("in save()");
        return repo.save(favoritePlace);
    }

    @Override
    public FavoritePlace update(FavoritePlace favoritePlace) {
        return null;
    }

    @Override
    public FavoritePlace findById(Long id) {
        log.info("In findById() method.");

        return repo.findById(id)
                .orElseThrow(() -> new BadIdException("No place with this id:" + id));;
    }

    @Override
    public FavoritePlace findAllByUserId(FavoritePlace favoritePlace) {
        return null;
    }

    @Override
    public void delete(FavoritePlace favoritePlace) {
    repo.delete(favoritePlace);
    }
}
