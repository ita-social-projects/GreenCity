package greencity.dto.openhours;

import com.fasterxml.jackson.annotation.JsonFormat;
import greencity.constant.ValidationConstants;
import greencity.dto.breaktime.BreakTimeDto;
import io.swagger.annotations.ApiModelProperty;
import java.time.DayOfWeek;
import java.time.LocalTime;
import javax.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"openTime", "closeTime"})
public class OpeningHoursUpdateDto {
    @NotNull(message = ValidationConstants.EMPTY_OPEN_TIME_VALUE)
    @JsonFormat(pattern = "HH:mm", shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(dataType = "java.lang.String")
    private LocalTime openTime;

    @NotNull(message = ValidationConstants.EMPTY_CLOSE_TIME_VALUE)
    @JsonFormat(pattern = "HH:mm", shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(dataType = "java.lang.String")
    private LocalTime closeTime;

    @NotNull(message = ValidationConstants.EMPTY_WEEK_DAY_VALUE)
    private DayOfWeek weekDay;

    private BreakTimeDto breakTime;
}
