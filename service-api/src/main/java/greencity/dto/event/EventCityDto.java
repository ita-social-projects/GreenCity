package greencity.dto.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventCityDto {
    private String cityNameEn;
    private String cityNameUk;
    private Long amountOfEvents;
}
