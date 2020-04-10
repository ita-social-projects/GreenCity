package greencity.mapping;

import greencity.dto.favoriteplace.FavoritePlaceDto;
import greencity.entity.FavoritePlace;
import greencity.entity.Place;
import greencity.entity.User;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * The class uses other {@code Autowired} mappers to convert {@link FavoritePlace} entity objects to {@link
 * FavoritePlaceDto} dto objects and vise versa.
 *
 * @author Zakhar Skaletskyi
 */
@AllArgsConstructor
@Component
public class FavoritePlaceDtoMapper implements MapperToDto<FavoritePlace, FavoritePlaceDto>,
    MapperToEntity<FavoritePlaceDto, FavoritePlace> {

    @Override
    public FavoritePlace convertToEntity(FavoritePlaceDto dto) {
        FavoritePlace favoritePlace = new FavoritePlace();
        favoritePlace.setId(dto.getFavoritePlaceId());
        favoritePlace.setName(dto.getName());
        favoritePlace.setUser(User.builder().id(dto.getFavoritePlaceId()).build());
        favoritePlace.setPlace(Place.builder().id(dto.getFavoritePlaceId()).build());
        return favoritePlace;
    }

    @Override
    public FavoritePlaceDto convertToDto(FavoritePlace entity) {
        FavoritePlaceDto favoritePlaceDto = new FavoritePlaceDto();
        favoritePlaceDto.setFavoritePlaceId(entity.getPlace().getId());
        favoritePlaceDto.setName(entity.getName());
        return favoritePlaceDto;
    }
}
