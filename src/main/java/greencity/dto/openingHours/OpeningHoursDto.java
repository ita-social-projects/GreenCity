package greencity.dto.openingHours;

import com.fasterxml.jackson.annotation.JsonFormat;
import greencity.entity.OpeningHours;
import greencity.entity.enums.WeekDay;
import java.time.LocalTime;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class OpeningHoursDto {

    @NotNull
    @JsonFormat(pattern = "HH:mm", shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(dataType = "java.lang.String")
    private LocalTime openTime;

    @NotNull
    @JsonFormat(pattern = "HH:mm", shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(dataType = "java.lang.String")
    private LocalTime closeTime;

    @NotNull private WeekDay weekDay;

    public OpeningHoursDto(OpeningHours openingHours) {
        this.openTime = openingHours.getOpenTime();
        this.closeTime = openingHours.getCloseTime();
        this.weekDay = openingHours.getWeekDay();
    }
}
