package greencity.dto.habitstatistic;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class GetHabitStatisticDto {
    Long amountOfUsersAcquired;
    List<HabitStatisticDto> habitStatisticDtoList;
}
