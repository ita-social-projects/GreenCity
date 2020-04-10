package greencity.mapping;

import greencity.dto.favoriteplace.FavoritePlaceDto;
import greencity.entity.FavoritePlace;
import greencity.entity.Place;
import greencity.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * The class uses other {@code Autowired} mappers to convert {@link FavoritePlace} entity objects to {@link
 * FavoritePlaceDto} dto objects and vise versa.
 *
 * @author Zakhar Skaletskyi
 */
@AllArgsConstructor
@Component
public class FavoritePlaceDtoMapper
    implements MapperToDto<FavoritePlace, FavoritePlaceDto>, MapperToEntity<FavoritePlaceDto, FavoritePlace> {
    @Override
    public FavoritePlace convertToEntity(FavoritePlaceDto dto) {
        FavoritePlace favoritePlace = FavoritePlace.builder().id(dto.getPlaceId()).name(dto.getName())
            .user(User.builder().id(dto.getPlaceId()).build()).place(Place.builder().id(dto.getPlaceId()).build())
            .build();
        return favoritePlace;
    }

    @Override
    public FavoritePlaceDto convertToDto(FavoritePlace entity) {
        FavoritePlaceDto favoritePlaceDto = new FavoritePlaceDto();
        favoritePlaceDto.setPlaceId(entity.getPlace().getId());
        favoritePlaceDto.setName(entity.getName());
        return favoritePlaceDto;
    }
}
