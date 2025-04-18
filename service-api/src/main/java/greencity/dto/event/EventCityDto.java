package greencity.dto.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventCityDto {
    private String cityEn;
    private String cityUk;
    private Long amountOfEvents;
}
