package greencity.entity;

import greencity.entity.localization.ShoppingListItemTranslation;
import java.util.List;
import java.util.Set;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
