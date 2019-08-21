package greencity.mapping;

import greencity.dto.place.AdminPlaceDto;
import greencity.entity.Place;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * The class uses other {@code Autowired} mappers to convert {@link Place} entity objects to {@link
 * AdminPlaceDto} dto objects and vise versa.
 */
@AllArgsConstructor
@Component
public class AdminPlaceMapper implements Mapper<Place, AdminPlaceDto> {

    /** Autowired mappers.*/
    private ModelMapper modelMapper;

    @Override
    public Place convertToEntity(AdminPlaceDto dto) {
        throw new NotImplementedException();
    }

    @Override
    public AdminPlaceDto convertToDto(Place entity) {
        AdminPlaceDto dto = modelMapper.map(entity, AdminPlaceDto.class);
        dto.getAuthor().setPlaceId(dto.getId());
        dto.getLocation().setPlaceId(dto.getId());
        dto.getOpeningHours().forEach(hours -> hours.setPlaceId(dto.getId()));
        return dto;
    }
}
