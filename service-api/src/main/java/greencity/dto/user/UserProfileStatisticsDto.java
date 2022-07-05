package greencity.dto.user;

import lombok.*;

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
