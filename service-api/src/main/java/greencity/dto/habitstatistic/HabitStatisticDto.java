package greencity.dto.habitstatistic;

import greencity.enums.HabitRate;
import java.time.ZonedDateTime;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class HabitStatisticDto {
    @NotNull
    @Min(1)
    private Long id;
    @NotEmpty
    private HabitRate habitRate;
    @NotEmpty
    private ZonedDateTime createDate;
    @NotEmpty
    private Integer amountOfItems;
    @NotEmpty
    private Long habitAssignId;
}
