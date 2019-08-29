package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import greencity.dto.favoritePlace.FavoritePlaceDto;
import greencity.entity.FavoritePlace;
import greencity.exception.BadIdAndEmailException;
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
    /**
     * @author Zakhar Skaletskyi
     *
     * {@inheritDoc}
     */
    public FavoritePlace save(FavoritePlace favoritePlace) {
        log.info(LogMessage.IN_SAVE, FavoritePlace.class.getName());

        if (!userService.existsByEmail(favoritePlace.getUser().getEmail())) {
            throw new BadIdException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL);
        }
        if (!placeService.existsById(favoritePlace.getPlace().getId())) {
            throw new BadIdException(ErrorMessage.PLACE_NOT_FOUND_BY_ID);
        }
        if (repo.existsByPlaceIdAndUserEmail(favoritePlace.getPlace().getId(), favoritePlace.getUser().getEmail())) {
            throw new BadIdAndEmailException(ErrorMessage.FAVORITE_PLACE_ALREADY_EXISTS);
        }
        favoritePlace.getUser().setId(userRepo.findIdByEmail(favoritePlace.getUser().getEmail()));
        return repo.save(favoritePlace);
    }

    /**
     * @author Zakhar Skaletskyi
     * <p>
     * {@inheritDoc}
     */
    @Override
    public FavoritePlaceDto save(FavoritePlaceDto favoritePlaceDto) {
        return modelMapper.map(save(modelMapper.map(favoritePlaceDto, FavoritePlace.class)), FavoritePlaceDto.class);
    }

    /**
     * @author Zakhar Skaletskyi
     * <p>
     * {@inheritDoc}
     */
    @Override
    public FavoritePlaceDto update(FavoritePlaceDto favoritePlaceDto) {
        log.info(LogMessage.IN_UPDATE, FavoritePlace.class.getName());
        if (!userService.existsByEmail(favoritePlaceDto.getUser().getEmail())) {
            throw new BadIdException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL);
        }
        if (!placeService.existsById(favoritePlaceDto.getPlace().getId())) {
            throw new BadIdException(ErrorMessage.PLACE_NOT_FOUND_BY_ID);
        }

        if (!repo.existsByPlaceIdAndUserEmail(favoritePlaceDto.getPlace().getId(), favoritePlaceDto.getUser().getEmail())) {//
            throw new BadIdException(ErrorMessage.FAVORITE_PLACE_NOT_FOUND);
        }
        FavoritePlace favoritePlace=modelMapper.map(favoritePlaceDto, FavoritePlace.class);
        favoritePlace.getUser().setId(userService.findIdByEmail(favoritePlace.getUser().getEmail()));
        favoritePlace.setId(repo.findByUserAndPlace(favoritePlace.getUser(), favoritePlace.getPlace()).getId());
        return modelMapper.map(repo.save(favoritePlace), FavoritePlaceDto.class);
    }

    /**
     * @author Zakhar Skaletskyi
     * <p>
     * {@inheritDoc}
     */
    @Override
    public List<FavoritePlaceDto> findAllByUserEmail(String email) {
        log.info(LogMessage.IN_FIND_ALL);
        if (!userService.existsByEmail(email)) {
            throw new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL);
        }
        List<FavoritePlace> favoritePlaces = repo.findAllByUserEmail(email);
        return favoritePlaces.stream().map(fp -> modelMapper.map(fp, FavoritePlaceDto.class)).collect(Collectors.toList());
    }

    /**
     * @author Zakhar Skaletskyi
     * <p>
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteByPlaceIdAndUserEmail(FavoritePlaceDto favoritePlaceDto) {
        log.info(LogMessage.IN_DELETE_BY_PLACE_ID_AND_USER_EMAIL);
        if (!repo.existsByPlaceIdAndUserEmail(favoritePlaceDto.getPlace().getId(), favoritePlaceDto.getUser().getEmail())) {
            throw new NotFoundException(ErrorMessage.FAVORITE_PLACE_NOT_FOUND);
        }
        repo.deleteByPlaceIdAndUserEmail(favoritePlaceDto.getPlace().getId(), favoritePlaceDto.getUser().getEmail());
    }
}
