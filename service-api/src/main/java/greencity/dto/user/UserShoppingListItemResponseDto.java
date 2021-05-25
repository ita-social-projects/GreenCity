package greencity.dto.user;

import greencity.enums.ShoppingListItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class UserShoppingListItemResponseDto {
    private Long id;
    private String text;
    private ShoppingListItemStatus status;
}
