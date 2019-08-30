package greencity.dto.openhours;

import com.fasterxml.jackson.annotation.JsonFormat;
import greencity.entity.enums.WeekDay;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalTime;
import javax.validation.constraints.NotNull;
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


    @NotNull private WeekDay weekDay;

}
