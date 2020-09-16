package greencity.dto.breaktime;

import com.fasterxml.jackson.annotation.JsonFormat;
import greencity.constant.ValidationConstants;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalTime;
import javax.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"startTime", "endTime"})
public class BreakTimeDto {
    @NotNull
    @JsonFormat(pattern = "HH:mm", shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(dataType = "java.lang.String")
    private LocalTime startTime;

    @NotNull
    @JsonFormat(pattern = "HH:mm", shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(dataType = "java.lang.String")
    private LocalTime endTime;
}

