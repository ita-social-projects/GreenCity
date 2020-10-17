package greencity.dto.openinghours;

import greencity.dto.breaktime.BreakTimeVO;
import greencity.dto.place.PlaceVO;
import java.time.DayOfWeek;
import java.time.LocalTime;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class OpeningHoursVO {
    private Long id;
    private LocalTime openTime;
    private LocalTime closeTime;
    private DayOfWeek weekDay;
    private BreakTimeVO breakTime;
    private PlaceVO place;
}
