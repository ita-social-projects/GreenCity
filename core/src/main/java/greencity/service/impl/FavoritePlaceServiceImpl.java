package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import greencity.dto.favoriteplace.FavoritePlaceDto;
import greencity.dto.place.PlaceByBoundsDto;
import greencity.dto.place.PlaceInfoDto;
import greencity.entity.FavoritePlace;
import greencity.entity.User;
import greencity.exception.exceptions.WrongIdException;
import greencity.repository.FavoritePlaceRepo;
import greencity.service.FavoritePlaceService;
import greencity.service.PlaceService;
import greencity.service.UserService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class FavoritePlaceServiceImpl implements FavoritePlaceService {
    private final FavoritePlaceRepo repo;
    private final UserService userService;
    private final PlaceService placeService;
    private final ModelMapper modelMapper;

    /**
     * {@inheritDoc}
     *
     * @author Zakhar Skaletskyi
     */
    @Override
    public FavoritePlaceDto save(FavoritePlaceDto favoritePlaceDto, String userEmail) {
        log.info(LogMessage.IN_SAVE, favoritePlaceDto);
        FavoritePlace favoritePlace = modelMapper.map(favoritePlaceDto, FavoritePlace.class);
        if (!placeService.existsById(favoritePlace.getPlace().getId())) {
            throw new WrongIdException(ErrorMessage.PLACE_NOT_FOUND_BY_ID);
        }
        if (repo.findByPlaceIdAndUserEmail(favoritePlaceDto.getPlaceId(), userEmail) != null) {
            throw new WrongIdException(String.format(
                ErrorMessage.FAVORITE_PLACE_ALREADY_EXISTS, favoritePlaceDto.getPlaceId(), userEmail));
        }
        favoritePlace.setUser(User.builder().email(userEmail).id(userService.findIdByEmail(userEmail)).build());
        return modelMapper.map(repo.save(favoritePlace), FavoritePlaceDto.class);
    }

    /**
     * {@inheritDoc}
     *
     * @author Zakhar Skaletskyi
     */
    @Override
    public FavoritePlaceDto update(FavoritePlaceDto favoritePlaceDto, String userEmail) {
        log.info(LogMessage.IN_UPDATE, favoritePlaceDto);

        FavoritePlace favoritePlace = repo.findByPlaceIdAndUserEmail(favoritePlaceDto.getPlaceId(), userEmail);
        if (favoritePlace == null) {
            throw new WrongIdException(ErrorMessage.FAVORITE_PLACE_NOT_FOUND + favoritePlaceDto.getPlaceId());
        }
        favoritePlace.setName(favoritePlaceDto.getName());
        return modelMapper.map(repo.save(favoritePlace), FavoritePlaceDto.class);
    }

    /**
     * {@inheritDoc}
     *
     * @author Zakhar Skaletskyi
     */
    @Override
    public List<FavoritePlaceDto> findAllByUserEmail(String email) {
        log.info(LogMessage.IN_FIND_ALL);
        return repo.findAllByUserEmail(email).stream().map(fp -> modelMapper.map(fp, FavoritePlaceDto.class))
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     *
     * @author Zakhar Skaletskyi
     */
    @Override
    @Transactional
    public Long deleteByUserEmailAndPlaceId(Long placeId, String userEmail) {
        log.info(LogMessage.IN_DELETE_BY_PLACE_ID_AND_USER_EMAIL, userEmail, placeId);
        FavoritePlace favoritePlace = repo.findByPlaceIdAndUserEmail(placeId, userEmail);
        if (favoritePlace == null) {
            throw new WrongIdException(ErrorMessage.FAVORITE_PLACE_NOT_FOUND);
        }
        repo.delete(favoritePlace);
        return favoritePlace.getId();
    }

    /**
     * {@inheritDoc}
     *
     * @author Zakhar Skaletskyi
     */
    @Override
    public FavoritePlace findByPlaceId(Long placeId) {
        log.info(LogMessage.IN_FIND_BY_PLACE_ID, placeId);
        FavoritePlace favoritePlace = repo.findByPlaceId(placeId);
        if (favoritePlace == null) {
            throw new WrongIdException(ErrorMessage.FAVORITE_PLACE_NOT_FOUND);
        }
        return favoritePlace;
    }

    /**
     * {@inheritDoc}
     *
     * @author Zakhar Skaletskyi
     */

    @Override
    public PlaceInfoDto getInfoFavoritePlace(Long placeId) {
        log.info(LogMessage.IN_GET_ACCESS_PLACE_AS_FAVORITE_PLACE, placeId);
        FavoritePlace favoritePlace = findByPlaceId(placeId);
        PlaceInfoDto placeInfoDto =
            modelMapper.map(
                placeService
                    .findById(favoritePlace.getPlace().getId()),
                PlaceInfoDto.class);
        placeInfoDto.setRate(placeService.averageRate(favoritePlace.getPlace().getId()));
        placeInfoDto.setName(favoritePlace.getName());
        return placeInfoDto;
    }

    /**
     * {@inheritDoc}
     *
     * @author Zakhar Skaletskyi
     */
    @Override
    public PlaceByBoundsDto getFavoritePlaceWithLocation(Long placeId, String email) {
        log.info(LogMessage.IN_GET_FAVORITE_PLACE_WITH_LOCATION, placeId, email);
        FavoritePlace favoritePlace = repo.findByPlaceIdAndUserEmail(placeId, email);
        if (favoritePlace == null) {
            throw new WrongIdException(ErrorMessage.FAVORITE_PLACE_NOT_FOUND);
        }
        return modelMapper.map(repo.findByPlaceIdAndUserEmail(placeId, email), PlaceByBoundsDto.class);
    }
}