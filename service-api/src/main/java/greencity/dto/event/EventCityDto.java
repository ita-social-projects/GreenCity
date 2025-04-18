package greencity.dto.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventCityDto {
    private String nameEn;
    private String nameUk;
    private Long amountOfEvents;
}
