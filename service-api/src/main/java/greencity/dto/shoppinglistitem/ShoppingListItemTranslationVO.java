package greencity.dto.shoppinglistitem;

import greencity.dto.translation.TranslationVO;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true, exclude = "shoppingListItemVO")
@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
public class ShoppingListItemTranslationVO extends TranslationVO {
    private ShoppingListItemVO shoppingListItemVO;
}
