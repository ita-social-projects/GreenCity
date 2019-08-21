package greencity.service.impl;

import greencity.entity.FavoritePlace;
import greencity.exception.NotFoundException;
import greencity.repository.FavoritePlaceRepo;
import greencity.service.FavoritePlaceService;
import greencity.exception.BadIdException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class FavoritePlaceServiceImpl implements FavoritePlaceService {
    private FavoritePlaceRepo repo;

    @Override
    public FavoritePlace save(FavoritePlace favoritePlace) {
        log.info("in save()");
        if ( repo.existsById(favoritePlace.getId()))
        {
            throw new BadIdException("Favorite place already exist.");
        }
        return repo.save(favoritePlace);
    }

    @Override
    public FavoritePlace update(FavoritePlace favoritePlace) {
        log.info("in update()");
        return repo.save(favoritePlace);
    }

    @Override
    public FavoritePlace findById(Long id) {
        log.info("In findById() method.");
        return repo.findById(id).orElseThrow(() -> new BadIdException("No place with this id:" + id));

    }

    @Override
    public List<FavoritePlace> findAllByUserId(Long userId) {
        log.info("In findAllByUserId() method.");
        List<FavoritePlace> favoritePlaces = null;
        try {
            favoritePlaces = repo.findAllByUserId(userId);
        } catch (NotFoundException e) {
            e.getMessage();
        }
        return favoritePlaces;
    }

    @Override
    public void deleteById(long id) {
        log.info("in delete()");
        if (!repo.existsById(id))
        {
            throw new BadIdException("Favorite place doesn't exist.");
        }
        repo.deleteById(id);
    }
}
