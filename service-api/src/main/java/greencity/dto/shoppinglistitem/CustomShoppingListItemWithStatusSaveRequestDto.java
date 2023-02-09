package greencity.dto.shoppinglistitem;

import greencity.enums.ShoppingListItemStatus;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class CustomShoppingListItemWithStatusSaveRequestDto extends CustomShoppingListItemSaveRequestDto {
    ShoppingListItemStatus status;
}
