package greencity.mapping;

import greencity.dto.location.LocationDto;
import greencity.dto.place.PlaceByBoundsDto;
import greencity.entity.FavoritePlace;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

/**
 * The class uses other {@code Autowired} mappers to convert
 * {@link FavoritePlace} entity objects to {@link PlaceByBoundsDto} dto objects.
 *
 * @author Zakhar Skaletskyi
 */

@Component
public class FavoritePlaceWithLocationMapper extends AbstractConverter<FavoritePlace, PlaceByBoundsDto> {
    @Override
    public PlaceByBoundsDto convert(FavoritePlace entity) {
        PlaceByBoundsDto placeByBoundsDto = new PlaceByBoundsDto();
        placeByBoundsDto.setId(entity.getId());
        placeByBoundsDto.setName(entity.getName());
        LocationDto location = new LocationDto();
        location.setId(entity.getPlace().getLocation().getId());
        location.setLng(entity.getPlace().getLocation().getLng());
        location.setLat(entity.getPlace().getLocation().getLat());
        location.setAddress(entity.getPlace().getLocation().getAddress());
        placeByBoundsDto.setLocation(location);
        return placeByBoundsDto;
    }
}
