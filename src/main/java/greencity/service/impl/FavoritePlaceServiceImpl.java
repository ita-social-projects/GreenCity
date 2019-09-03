package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import greencity.dto.favoriteplace.FavoritePlaceDto;
import greencity.dto.favoriteplace.FavoritePlaceShowDto;
import greencity.dto.place.PlaceInfoDto;
import greencity.entity.FavoritePlace;
import greencity.entity.User;
import greencity.exception.BadIdAndEmailException;
import greencity.exception.BadIdException;
import greencity.exception.NotFoundException;
import greencity.mapping.FavoritePlaceDtoMapper;
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
    private FavoritePlaceRepo repo;
    private UserService userService;
    private PlaceService placeService;
    FavoritePlaceDtoMapper favoritePlaceDtoMapper;
    ModelMapper modelMapper;

    @Override
    /**
     * @author Zakhar Skaletskyi
     *
     * {@inheritDoc}
     */
    public FavoritePlaceDto save(FavoritePlaceDto favoritePlaceDto, String userEmail) {
        log.info(LogMessage.IN_SAVE, favoritePlaceDto);
        FavoritePlace favoritePlace = favoritePlaceDtoMapper.convertToEntity(favoritePlaceDto);
        if (!placeService.existsById(favoritePlace.getPlace().getId())) {
            throw new BadIdException(ErrorMessage.PLACE_NOT_FOUND_BY_ID);
        }
        if (repo.existsByPlaceIdAndUserEmail(favoritePlace.getPlace().getId(), userEmail)) {
            throw new BadIdAndEmailException(ErrorMessage.FAVORITE_PLACE_ALREADY_EXISTS);
        }
        favoritePlace.setUser(User.builder().email(userEmail).id(userService.findIdByEmail(userEmail)).build());
        return favoritePlaceDtoMapper.convertToDto(repo.save(favoritePlace));
    }

    /**
     * {@inheritDoc}
     *
     * @author Zakhar Skaletskyi
     */
    @Override
    public FavoritePlaceDto update(FavoritePlaceDto favoritePlaceDto, String userEmail) {
        log.info(LogMessage.IN_UPDATE, favoritePlaceDto);
        if (!placeService.existsById(favoritePlaceDto.getPlaceId())) {
            throw new BadIdException(ErrorMessage.PLACE_NOT_FOUND_BY_ID + favoritePlaceDto.getPlaceId());
        }
        if (!repo.existsByPlaceIdAndUserEmail(favoritePlaceDto.getPlaceId(), userEmail)) {
            throw new BadIdAndEmailException(ErrorMessage.FAVORITE_PLACE_NOT_FOUND);
        }
        FavoritePlace favoritePlace = favoritePlaceDtoMapper.convertToEntity(favoritePlaceDto);
        favoritePlace.setUser(User.builder().id(userService.findIdByEmail(userEmail)).email(userEmail).build());
        favoritePlace.setId(repo.findByUserAndPlace(favoritePlace.getUser(), favoritePlace.getPlace()).getId());
        return favoritePlaceDtoMapper.convertToDto(repo.save(favoritePlace));
    }

    /**
     * {@inheritDoc}
     *
     * @author Zakhar Skaletskyi
     */
    @Override
    public List<FavoritePlaceShowDto> findAllByUserEmail(String email) {
        log.info(LogMessage.IN_FIND_ALL);
        List<FavoritePlace> favoritePlaces = repo.findAllByUserEmail(email);
        return favoritePlaces.stream().map(fp -> modelMapper.map(fp, FavoritePlaceShowDto.class))
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     *
     * @author Zakhar Skaletskyi
     */
    @Override
    @Transactional
    public int deleteByPlaceIdAndUserEmail(Long placeId, String userEmail) {
        log.info(LogMessage.IN_DELETE_BY_PLACE_ID_AND_USER_EMAIL);
        if (!repo.existsByPlaceIdAndUserEmail(placeId, userEmail)) {
            throw new NotFoundException(ErrorMessage.FAVORITE_PLACE_NOT_FOUND);
        }
        return repo.deleteByPlaceIdAndUserEmail(placeId, userEmail);
    }

    /**
     * {@inheritDoc}
     *
     * @author Zakhar Skaletskyi
     */
    @Override
    public FavoritePlace findById(Long id) {
        log.info(LogMessage.IN_FIND_BY_ID);
        return repo.findById(id).orElseThrow(() -> new BadIdException(ErrorMessage.FAVORITE_PLACE_NOT_FOUND));
    }

    /**
     * {@inheritDoc}
     *
     * @author Zakhar Skaletskyi
     */

    @Override
    public PlaceInfoDto getInfoFavoritePlace(Long favoritePlaceId) {
        log.info(LogMessage.IN_GET_ACCESS_PLACE_AS_FAVORITE_PLACE, favoritePlaceId);
        FavoritePlace favoritePlace = findById(favoritePlaceId);
        PlaceInfoDto placeInfoDto =
            modelMapper.map(
                placeService
                    .findById(favoritePlace.getPlace().getId()),
                PlaceInfoDto.class);
        placeInfoDto.setRate(placeService.averageRate(favoritePlace.getPlace().getId()));
        placeInfoDto.setName(favoritePlace.getName());
        return placeInfoDto;
    }
}