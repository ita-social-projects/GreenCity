package greencity.dto.habit;

import greencity.dto.user.UserShoppingListItemResponseDto;
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
    private Long id;
    private Long userId;
    private Long habit;
    private HabitAssignStatus status;
    private Integer workingDays;
    private Integer duration;
    List<UserShoppingListItemResponseDto> userShoppingListItems;
}
