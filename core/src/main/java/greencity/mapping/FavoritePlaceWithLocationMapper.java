package greencity.mapping;

import greencity.dto.place.PlaceByBoundsDto;
import greencity.entity.FavoritePlace;
import lombok.AllArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

/**
 * The class uses other {@code Autowired} mappers to convert {@link FavoritePlace} entity objects to {@link
 * PlaceByBoundsDto} dto objects.
 *
 * @author Zakhar Skaletskyi
 */

@Component
public class FavoritePlaceWithLocationMapper extends AbstractConverter<FavoritePlace, PlaceByBoundsDto> {
    @Override
    public PlaceByBoundsDto convert(FavoritePlace entity) {
        return PlaceByBoundsDto.builder()
            .id(entity.getId())
            .name(entity.getName())
            .location(entity.getPlace().getLocation())
            .build();
    }
}
