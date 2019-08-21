package greencity.mapping;

import greencity.dto.location.LocationDto;
import greencity.entities.Location;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * The class converts {@link Location} entity objects to {@link LocationDto} dto objects and vise
 * versa.
 */
@AllArgsConstructor
@Component
public class LocationMapper implements Mapper<Location, LocationDto> {

    private ModelMapper modelMapper;

    @Override
    public Location convertToEntity(LocationDto dto) {
        return null;
    }

    @Override
    public LocationDto convertToDto(Location entity) throws IllegalArgumentException {
        LocationDto dto = modelMapper.map(entity, LocationDto.class);
        dto.setPlaceId(entity.getPlace().getId());
        return dto;
    }
}
