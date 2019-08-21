package greencity.mapping;

import greencity.dto.openhours.OpenHoursDto;
import greencity.entities.OpeningHours;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class OpenHoursMapper implements Mapper<OpeningHours, OpenHoursDto> {

    private ModelMapper modelMapper;

    @Override
    public OpeningHours convertToEntity(OpenHoursDto dto) {
        return null;
    }

    @Override
    public OpenHoursDto convertToDto(OpeningHours entity) {
        OpenHoursDto dto = modelMapper.map(entity, OpenHoursDto.class);
        dto.setPlaceId(entity.getPlace().getId());
        return dto;
    }
}
