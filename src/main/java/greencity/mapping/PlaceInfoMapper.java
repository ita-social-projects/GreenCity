package greencity.mapping;

import greencity.dto.place.PlaceInfoDto;
import greencity.entities.Location;
import greencity.entities.Place;
import greencity.entities.User;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class PlaceInfoMapper implements Mapper<Place, PlaceInfoDto> {

    private ModelMapper modelMapper;
    private LocationMapper locationMapper;
    private OpenHoursMapper hoursMapper;
    private UserMapper userMapper;

    @Override
    public Place convertToEntity(PlaceInfoDto dto) {
        return null;
    }

    @Override
    public PlaceInfoDto convertToDto(Place entity) {
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
