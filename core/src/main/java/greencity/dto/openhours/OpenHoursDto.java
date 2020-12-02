package greencity.dto.openhours;

import com.fasterxml.jackson.annotation.JsonFormat;
import greencity.dto.breaktime.BreakTimeDto;
import java.time.DayOfWeek;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OpenHoursDto {
    private Long id;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime openTime;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime closeTime;
    private DayOfWeek weekDay;
    private BreakTimeDto breakTime;
}
