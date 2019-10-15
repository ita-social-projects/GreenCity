package greencity.mapping;

import greencity.dto.favoriteplace.FavoritePlaceDto;
import greencity.entity.FavoritePlace;
import greencity.entity.Place;
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
    private ModelMapper modelMapper;

    @Override
    public FavoritePlace convertToEntity(FavoritePlaceDto dto) {
        FavoritePlace favoritePlace = modelMapper.map(dto, FavoritePlace.class);
        favoritePlace.setPlace(Place.builder().id(dto.getPlaceId()).build());
        return favoritePlace;
    }

    @Override
    public FavoritePlaceDto convertToDto(FavoritePlace entity) {
        FavoritePlaceDto favoritePlaceDto = modelMapper.map(entity, FavoritePlaceDto.class);
        favoritePlaceDto.setPlaceId(entity.getPlace().getId());
        return favoritePlaceDto;
    }
}
