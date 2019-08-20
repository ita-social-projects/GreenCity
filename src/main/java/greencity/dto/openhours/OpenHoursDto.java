package greencity.dto.openhours;

import greencity.entities.enums.WeekDay;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OpenHoursDto {

    private Long id;
    private LocalTime openTime;
    private LocalTime closeTime;
    private WeekDay weekDay;
    private Long placeId;
}
