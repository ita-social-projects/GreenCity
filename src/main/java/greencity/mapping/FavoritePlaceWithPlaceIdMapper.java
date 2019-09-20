package greencity.mapping;

import greencity.constant.ErrorMessage;
import greencity.dto.favoriteplace.FavoritePlaceDto;
import greencity.entity.FavoritePlace;
import greencity.exception.NotImplementedMethodException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;


/**
 * The class uses other {@code Autowired} mappers to convert {@link FavoritePlace} entity objects to {@link
 * FavoritePlaceDto} dto objects.
 *
 * @author Zakhar Skaletskyi
 */
@AllArgsConstructor
@Component
public class FavoritePlaceWithPlaceIdMapper implements Mapper<FavoritePlace, FavoritePlaceDto> {
    @Override
    public FavoritePlace convertToEntity(FavoritePlaceDto dto) {
        throw new NotImplementedMethodException(ErrorMessage.NOT_IMPLEMENTED_METHOD);
    }

    @Override
    public FavoritePlaceDto convertToDto(FavoritePlace entity) {
        FavoritePlaceDto favoritePlaceDto = new FavoritePlaceDto();
        favoritePlaceDto.setPlaceId(entity.getPlace().getId());
        favoritePlaceDto.setName(entity.getName());
        return favoritePlaceDto;
    }
}
