package greencity.dto.user;

import greencity.dto.shoppinglistitem.ShoppingListItemVO;
import greencity.dto.habit.HabitAssignVO;
import greencity.enums.ShoppingListItemStatus;
import java.time.LocalDateTime;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@Setter
@Builder
public class UserShoppingListItemVO {
    private Long id;

    private HabitAssignVO habitAssign;

    private ShoppingListItemVO shoppingListItemVO;

    @Builder.Default
    private ShoppingListItemStatus status = ShoppingListItemStatus.ACTIVE;

    private LocalDateTime dateCompleted;
}
