package greencity.dto.openingHours;

import com.fasterxml.jackson.annotation.JsonFormat;
import greencity.entity.enums.WeekDays;
import java.time.LocalTime;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpeningHoursDto {

    @NotNull
    @JsonFormat(pattern = "HH:mm", shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(dataType = "java.lang.String")
    private LocalTime openTime;

    @NotNull
    @JsonFormat(pattern = "HH:mm", shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(dataType = "java.lang.String")
    private LocalTime closeTime;

    @NotNull private WeekDays weekDays;

}
