package greencity.dto.openingHours;

import greencity.entity.OpeningHours;
import greencity.entity.enums.WeekDays;
import java.time.LocalTime;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class OpeningHoursDto {

    @NotNull private LocalTime openTime;

    @NotNull private LocalTime closeTime;

    @NotNull
    private WeekDays weekDays;

    public OpeningHoursDto(OpeningHours openingHours) {
        this.openTime = openingHours.getOpenTime();
        this.closeTime = openingHours.getCloseTime();
        this.weekDays = openingHours.getWeekDays();
    }
}
