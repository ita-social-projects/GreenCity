package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import greencity.dto.favoriteplace.FavoritePlaceDto;
import greencity.dto.favoriteplace.FavoritePlaceShowDto;
import greencity.dto.place.PlaceByBoundsDto;
import greencity.dto.place.PlaceInfoDto;
import greencity.entity.FavoritePlace;
import greencity.entity.User;
import greencity.entity.enums.PlaceStatus;
import greencity.exception.BadIdException;
import greencity.mapping.FavoritePlaceDtoMapper;
import greencity.mapping.FavoritePlaceWithLocationMapper;
import greencity.mapping.FavoritePlaceWithPlaceIdMapper;
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
    FavoritePlaceWithLocationMapper favoritePlaceWithLocationMapper;
    ModelMapper modelMapper;
    private static final PlaceStatus APPROVED_STATUS = PlaceStatus.APPROVED;
    FavoritePlaceWithPlaceIdMapper favoritePlaceWithPlaceIdMapper;

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

        favoritePlace.setUser(User.builder().email(userEmail).id(userService.findIdByEmail(userEmail)).build());
        return favoritePlaceDtoMapper.convertToDto(repo.save(favoritePlace));
    }

    /**
     * {@inheritDoc}
     *
     * @author Zakhar Skaletskyi
     */
    @Override
    public FavoritePlaceShowDto update(FavoritePlaceShowDto favoritePlaceShowDto, String userEmail) {
        log.info(LogMessage.IN_UPDATE, favoritePlaceShowDto);

        FavoritePlace favoritePlace = repo.findByIdAndUserEmail(favoritePlaceShowDto.getId(), userEmail);
        if (favoritePlace == null) {
            throw new BadIdException(ErrorMessage.FAVORITE_PLACE_NOT_FOUND + favoritePlaceShowDto.getId());
        }
        favoritePlace.setName(favoritePlaceShowDto.getName());
        return modelMapper.map(repo.save(favoritePlace), FavoritePlaceShowDto.class);
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
    public Long deleteByUserEmailAndFavoriteIdOrPlaceId(Long id, String userEmail) {
        log.info(LogMessage.IN_DELETE_BY_PLACE_ID_AND_USER_EMAIL + "id=" + id + " email=" + userEmail);
        FavoritePlace favoritePlace;
        if (id > 0) {
            favoritePlace = repo.findByIdAndUserEmail(id, userEmail);
        } else {
            id *= -1;
            favoritePlace = repo.findByPlaceIdAndUserEmail(id, userEmail);
        }

        if (favoritePlace == null) {
            throw new BadIdException(ErrorMessage.FAVORITE_PLACE_NOT_FOUND);
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

    /**
     * {@inheritDoc}
     *
     * @author Zakhar Skaletskyi
     */
    @Override
    public PlaceByBoundsDto getFavoritePlaceWithLocation(Long id, String email) {
        log.info("in getFavoritePlaceWithLocation() =" + id);
        return favoritePlaceWithLocationMapper.convertToDto(repo.findByIdAndUserEmail(id, email));
    }

    @Override
    public List<FavoritePlaceDto> getFavoritePlaceWithPlaceId(String email) {
        log.info("in getFavoritePlaceWithPlaceId email=" + email);
        return repo.findAllByUserEmail(email).stream().map(res -> favoritePlaceWithPlaceIdMapper.convertToDto(res))
            .collect(Collectors.toList());
    }
}