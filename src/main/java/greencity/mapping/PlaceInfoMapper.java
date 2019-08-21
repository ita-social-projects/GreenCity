package greencity.mapping;

import greencity.dto.place.PlaceInfoDto;
import greencity.entities.Place;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * The class uses other {@code Autowired} mappers to convert {@link Place} entity objects to {@link
 * PlaceInfoDto} dto objects and vise versa.
 */
@AllArgsConstructor
@Component
public class PlaceInfoMapper implements Mapper<Place, PlaceInfoDto> {

    /** Autowired mappers.*/
    private ModelMapper modelMapper;

    @Override
    public Place convertToEntity(PlaceInfoDto dto) {
        return null;
    }

    @Override
    public PlaceInfoDto convertToDto(Place entity) {
        return modelMapper.map(entity, PlaceInfoDto.class);
    }
}
