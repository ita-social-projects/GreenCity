package greencity.entity;

import java.util.List;
import java.util.Set;
import javax.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "habits")
@EqualsAndHashCode(
    exclude = {"habitAssigns", "habitTranslations", "tags", "shoppingListItems"})
@ToString(
    exclude = {"habitAssigns", "habitTranslations", "tags", "shoppingListItems"})
public class Habit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(name = "image", nullable = false)
    private String image;

    @Column(name = "default_duration", nullable = false)
    private Integer defaultDuration;

    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CustomShoppingListItem> customShoppingListItems;

    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HabitTranslation> habitTranslations;

    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL)
    private List<HabitAssign> habitAssigns;

    @ManyToMany
    @JoinTable(
        name = "habit_shopping_list_items",
        joinColumns = @JoinColumn(name = "habit_id"),
        inverseJoinColumns = @JoinColumn(name = "shopping_list_item_id"))
    private Set<ShoppingListItem> shoppingListItems;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "habits_tags",
        joinColumns = @JoinColumn(name = "habit_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags;
}
