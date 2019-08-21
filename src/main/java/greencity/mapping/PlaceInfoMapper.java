package greencity.mapping;

import greencity.dto.place.PlaceInfoDto;
import greencity.entities.Place;
import java.util.stream.Collectors;
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
    private LocationMapper locationMapper;
    private OpenHoursMapper hoursMapper;
    private UserMapper userMapper;

    @Override
    public Place convertToEntity(PlaceInfoDto dto) {
        return null;
    }

    //TODO throws IllegalArgumentException ???????????????????????????????????????????????????????
    @Override
    public PlaceInfoDto convertToDto(Place entity) throws IllegalArgumentException {
        PlaceInfoDto dto = modelMapper.map(entity, PlaceInfoDto.class);
        dto.setAddress(locationMapper.convertToDto(entity.getLocation()));
        dto.setAuthor(userMapper.convertToDto(entity.getAuthor()));
        dto.setOpenHours(
                entity.getOpeningHours().stream()
                        .map(h -> hoursMapper.convertToDto(h))
                        .collect(Collectors.toList()));
        return dto;
    }
}
