package greencity.entity.localization;

import greencity.entity.ShoppingListItem;
import greencity.entity.Translation;
import lombok.*;
import lombok.experimental.SuperBuilder;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "shopping_list_item_translations")
@EqualsAndHashCode(callSuper = true, exclude = "shoppingListItem")
@SuperBuilder
@NoArgsConstructor
public class ShoppingListItemTranslation extends Translation {
    @Getter
    @Setter
    @ManyToOne
    private ShoppingListItem shoppingListItem;
}
