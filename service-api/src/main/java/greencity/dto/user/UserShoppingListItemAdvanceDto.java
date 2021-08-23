package greencity.dto.user;

import greencity.enums.ShoppingListItemStatus;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@Setter
@Builder
public class UserShoppingListItemAdvanceDto {
    private Long id;
    private Long shoppingListItemId;
    private ShoppingListItemStatus status;
    private LocalDateTime dateCompleted;
    private String content;
}
