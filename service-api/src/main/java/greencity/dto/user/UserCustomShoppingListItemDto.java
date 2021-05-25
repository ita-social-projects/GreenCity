package greencity.dto.user;

import greencity.dto.shoppinglistitem.CustomShoppingListItemRequestDto;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class UserCustomShoppingListItemDto {
    private CustomShoppingListItemRequestDto customShoppingListItem;
}
