package greencity.entity;

import greencity.dto.goal.ShoppingListDtoResponse;
import greencity.entity.localization.GoalTranslation;
import java.util.List;
import javax.persistence.*;
import lombok.*;

@Entity
@SqlResultSetMapping(
    name = "groupDetailsMapping",
    classes = {
        @ConstructorResult(
            targetClass = ShoppingListDtoResponse.class,
            columns = {
                @ColumnResult(name = "status",type = String.class),
                @ColumnResult(name = "text",type = String.class),
                @ColumnResult(name = "goal_id",type = Long.class),
                @ColumnResult(name = "custom_goal_id",type = Long.class)
            }
        )
    }
)

@NamedNativeQuery(name = "Goal.getShoppingList",
    query = " SELECT ug.status, gt.content as text, ug.goal_id, ug.custom_goal_id FROM user_goals AS ug "
    + " JOIN goal_translations AS gt ON gt.goal_id = ug.goal_id "
    + " JOIN languages AS l ON l.id = gt.language_id "
    + " WHERE ug.goal_id IS NOT NULL AND "
    + " ug.user_id = :userId AND l.code = :languageCode "
    + " UNION "
    + " SELECT ug.status, cg.text, ug.goal_id, ug.custom_goal_id "
    + " FROM user_goals AS ug "
    + " JOIN custom_goals AS cg ON cg.id = ug.custom_goal_id "
    + " WHERE ug.custom_goal_id IS NOT NULL AND ug.user_id = :userId ",
    resultSetMapping = "groupDetailsMapping")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(
    exclude = {"userGoals"})
@EqualsAndHashCode(exclude = {"userGoals", "translations"})
@Table(name = "goals")
@Builder
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "goal", fetch = FetchType.LAZY)
    private List<UserGoal> userGoals;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "goal", cascade = {CascadeType.REMOVE, CascadeType.REFRESH})
    private List<GoalTranslation> translations;
}
