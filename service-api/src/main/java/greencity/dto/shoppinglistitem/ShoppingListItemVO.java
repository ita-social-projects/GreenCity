package greencity.dto.shoppinglistitem;

import greencity.dto.user.UserShoppingListItemVO;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@Setter
@Builder
public class ShoppingListItemVO {
    private Long id;

    private List<UserShoppingListItemVO> userShoppingListItemsVO;

    private List<ShoppingListItemTranslationVO> translations;
}
