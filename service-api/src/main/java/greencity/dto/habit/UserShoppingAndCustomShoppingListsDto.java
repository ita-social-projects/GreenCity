package greencity.dto.habit;

import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.dto.user.UserShoppingListItemResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserShoppingAndCustomShoppingListsDto {
    @Valid
    List<@Valid UserShoppingListItemResponseDto> userShoppingListItemDto;
    @Valid
    List<@Valid CustomShoppingListItemResponseDto> customShoppingListItemDto;
}
