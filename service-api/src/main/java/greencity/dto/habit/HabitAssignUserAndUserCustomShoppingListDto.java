package greencity.dto.habit;

import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.dto.user.UserShoppingListItemResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitAssignUserAndUserCustomShoppingListDto {
    List<UserShoppingListItemResponseDto> userShoppingListItemDto;
    List<CustomShoppingListItemResponseDto> customShoppingListItemDto;
}
