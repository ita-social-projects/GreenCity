package greencity.dto.place;

import greencity.entity.enums.PlaceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatusDto {
    private PlaceStatus[] statuses;
}