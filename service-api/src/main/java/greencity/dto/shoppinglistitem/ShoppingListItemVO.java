package greencity.dto.shoppinglistitem;

import greencity.dto.user.UserShoppingListItemVO;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.ToString;
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
