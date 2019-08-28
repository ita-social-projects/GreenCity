package greencity.service.impl;

import greencity.dto.favoritePlace.FavoritePlaceDto;
import greencity.dto.favoritePlace.FavoritePlaceUpdateDto;
import greencity.entity.FavoritePlace;

import greencity.entity.Place;
import greencity.entity.User;
import greencity.exception.NotFoundException;
import greencity.repository.FavoritePlaceRepo;
import greencity.repository.UserRepo;
import greencity.service.FavoritePlaceService;
import greencity.exception.BadIdException;
import greencity.service.PlaceService;
import greencity.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class FavoritePlaceServiceImpl implements FavoritePlaceService {
    private FavoritePlaceRepo repo;
    private UserRepo userRepo;
    private UserService userService;
    private PlaceService placeService;
    private ModelMapper modelMapper;

    @Override
    public FavoritePlace save(FavoritePlace favoritePlace) {
        log.info("in save(FavoritePlace favoritePlace)");
        if (repo.existsByPlaceIdAndUserEmail(favoritePlace.getPlace().getId(), favoritePlace.getUser().getEmail())) {  //змінити  запит
            throw new BadIdException("Favorite place already exist."); //перенести в константи повідоення
        }
        if (!userService.existsByEmail(favoritePlace.getUser().getEmail())) {
            throw new BadIdException("User not exist.");
        }
        if (!placeService.existsById(favoritePlace.getPlace().getId())) {
            throw new BadIdException("Place not exist.");
        }

        return repo.save(favoritePlace);
    }


    @Override
    public FavoritePlaceDto save(FavoritePlaceDto favoritePlaceDto) {
        log.info("in save(FavoritePlaceDto favoritePlaceDto)");

        if (repo.existsByPlaceIdAndUserEmail(favoritePlaceDto.getPlace().getId(), favoritePlaceDto.getUser().getEmail())) {//
            throw new BadIdException("Favorite place already exist.");
        }
        if (!userService.existsByEmail(favoritePlaceDto.getUser().getEmail())) {
            throw new NotFoundException("User not exist.");
        }
        if (!placeService.existsById(favoritePlaceDto.getPlace().getId())) {
            throw new BadIdException("Place not exist.");
        }
        favoritePlaceDto.getUser().setId(userRepo.findIdByEmail(favoritePlaceDto.getUser().getEmail()));
        return modelMapper.map(repo.save(modelMapper.map(favoritePlaceDto, FavoritePlace.class)), FavoritePlaceDto.class);
    }


    @Override
    public FavoritePlaceUpdateDto update(FavoritePlaceUpdateDto favoritePlaceUpdateDto) {
        log.info("in update()");
        if (!repo.existsByPlaceIdAndUserEmail(favoritePlaceUpdateDto.getPlace().getId(), favoritePlaceUpdateDto.getUser().getEmail())) {//
            throw new BadIdException("Favorite place not exist.");
        }
        if (!userService.existsByEmail(favoritePlaceUpdateDto.getUser().getEmail())) {
            throw new NotFoundException("User not exist.");
        }
        if (!placeService.existsById(favoritePlaceUpdateDto.getPlace().getId())) {
            throw new BadIdException("Place not exist.");
        }
        favoritePlaceUpdateDto.getUser().setId(userService.findIdByEmail(favoritePlaceUpdateDto.getUser().getEmail()));
        favoritePlaceUpdateDto.setId(repo.findByUserAndPlace(modelMapper.map(favoritePlaceUpdateDto.getUser(), User.class), modelMapper.map(favoritePlaceUpdateDto.getPlace(), Place.class)).getId());
        return modelMapper.map(repo.save(modelMapper.map(favoritePlaceUpdateDto, FavoritePlace.class)), FavoritePlaceUpdateDto.class);
    }

    @Override
    public FavoritePlace findById(Long id) {
        log.info("In findById() method.");
        return repo.findById(id).orElseThrow(() -> new NotFoundException("No favorite place with this id:" + id));

    }

    @Override
    public List<FavoritePlaceDto> findAllByUserEmail(String email) {
        log.info("In findAllByUserEmail() method.");
        if (!userService.existsByEmail(email)) {
            throw new NotFoundException("User not exist.");
        }
        List<FavoritePlace> favoritePlaces = repo.findAllByUserEmail(email);
        return favoritePlaces.stream().map(fp -> modelMapper.map(fp, FavoritePlaceDto.class)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteByPlaceIdAndUserEmail(FavoritePlaceDto favoritePlaceDto) {
        log.info("in deleteByPlaceIdAndUserEmail()");
        if (!repo.existsByPlaceIdAndUserEmail(favoritePlaceDto.getPlace().getId(), favoritePlaceDto.getUser().getEmail())) {
            throw new NotFoundException("Favorite place not exist.");
        }
        repo.deleteByPlaceIdAndUserEmail(favoritePlaceDto.getPlace().getId(), favoritePlaceDto.getUser().getEmail());
    }
}
