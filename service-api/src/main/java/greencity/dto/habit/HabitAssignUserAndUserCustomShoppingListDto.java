package greencity.dto.habit;

import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.dto.user.UserShoppingListItemResponseDto;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@Setter
@Builder
public class HabitAssignUserAndUserCustomShoppingListDto {
    List<UserShoppingListItemResponseDto> userShoppingListItemsDto;
    List<CustomShoppingListItemResponseDto> customShoppingListItemDto;
}
