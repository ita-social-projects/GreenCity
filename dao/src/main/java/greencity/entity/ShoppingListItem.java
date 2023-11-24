package greencity.entity;

import greencity.entity.localization.ShoppingListItemTranslation;
import java.util.List;
import java.util.Set;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(
    exclude = {"userShoppingListItems"})
@EqualsAndHashCode(exclude = {"userShoppingListItems", "translations"})
@Table(name = "shopping_list_items")
@Builder
public class ShoppingListItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "shoppingListItem", fetch = FetchType.LAZY)
    private List<UserShoppingListItem> userShoppingListItems;

    @ManyToMany(mappedBy = "shoppingListItems", fetch = FetchType.LAZY)
    private Set<Habit> habits;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "shoppingListItem", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<ShoppingListItemTranslation> translations;
}
