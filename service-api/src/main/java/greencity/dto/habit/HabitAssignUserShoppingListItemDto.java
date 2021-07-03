package greencity.dto.habit;

import greencity.dto.user.UserShoppingListItemAdvanceDto;
import greencity.enums.HabitAssignStatus;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class HabitAssignUserShoppingListItemDto {
    private Long habitAssignId;
    private Long userId;
    private Long habitId;
    private HabitAssignStatus status;
    private Integer workingDays;
    private Integer duration;
    List<UserShoppingListItemAdvanceDto> userShoppingListItemsDto;
}
