package greencity.mapping;

import greencity.dto.place.PlaceAddDto;
import greencity.entity.Place;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;


/**
 * The class uses other {@code Autowired} mappers to convert {@link Place} entity objects to {@link
 * PlaceAddDto} dto objects and vise versa.
 *
 * @author Kateryna Horokh
 */
@AllArgsConstructor
@Component
public class PlaceAddDtoMapper implements Mapper<Place, PlaceAddDto> {
    /**
     * Autowired mappers.
     */
    private ModelMapper modelMapper;

    @Override
    public Place convertToEntity(PlaceAddDto dto) {
        Place place = modelMapper.map(dto, Place.class);
        place.getOpeningHoursList().forEach(h -> h.setPlace(place));
        return place;
    }

    @Override
    public PlaceAddDto convertToDto(Place entity) {
        throw new NotImplementedException();
    }
}
