package greencity.service.impl;

import greencity.dto.favoritePlace.FavoritePlaceDto;
import greencity.entity.FavoritePlace;
import greencity.exception.NotFoundException;
import greencity.repository.FavoritePlaceRepo;
import greencity.repository.UserRepo;
import greencity.service.FavoritePlaceService;
import greencity.exception.BadIdException;
import greencity.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class FavoritePlaceServiceImpl implements FavoritePlaceService {
    private FavoritePlaceRepo favoritePlaceRepo;
    private UserRepo userRepo;
    private UserService userService;
    private Places

    @Override
    public FavoritePlace save(FavoritePlace favoritePlace) {
        log.info("in save()");
        if ( favoritePlaceRepo.existsByPlaceId(favoritePlace.getId()))
        {
            throw new BadIdException("Favorite place already exist.");
        }
        return favoritePlaceRepo.save(favoritePlace);
    }
    @Override
    public FavoritePlace save(FavoritePlaceDto favoritePlaceDto) {
        log.info("in save()");
        if ( favoritePlaceRepo.existsByPlaceId(favoritePlaceDto.getPlaceId()))
        {
            throw new BadIdException("Favorite place already exist.");
        }
        if ( userRepo.existsById(favoritePlaceDto.getUserId()))
        {
            throw new BadIdException("User not exist.");
        }

        return favoritePlaceRepo.save(FavoritePlace.builder()
                .name(favoritePlaceDto.getName())
                .place(placeService.findById(favoritePlaceDto.getPlaceId()))
                .user(userService.findById(favoritePlaceDto.getUserId())));
    }

    @Override
    public FavoritePlace update(FavoritePlace favoritePlace) {
        log.info("in update()");
        return favoritePlaceRepo.save(favoritePlace);
    }

    @Override
    public FavoritePlace findById(Long id) {
        log.info("In findById() method.");
        return favoritePlaceRepo.findById(id).orElseThrow(() -> new BadIdException("No place with this id:" + id));

    }

    @Override
    public List<FavoritePlace> findAllByUserId(Long userId) {
        log.info("In findAllByUserId() method.");
        List<FavoritePlace> favoritePlaces = null;
        try {
            favoritePlaces = favoritePlaceRepo.findAllByUserId(userId);
        } catch (NotFoundException e) {
            e.getMessage();
        }
        return favoritePlaces;
    }

    @Override
    public void deleteById(long id) {
        log.info("in delete()");
        if (!favoritePlaceRepo.existsById(id))
        {
            throw new BadIdException("Favorite place doesn't exist.");
        }
        favoritePlaceRepo.deleteById(id);
    }
}
