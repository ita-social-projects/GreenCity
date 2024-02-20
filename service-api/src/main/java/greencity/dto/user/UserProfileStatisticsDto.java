package greencity.dto.user;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class UserProfileStatisticsDto {
    private Long amountHabitsInProgress;

    private Long amountHabitsAcquired;

    private Long amountPublishedNews;
}
