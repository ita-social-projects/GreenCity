package greencity.entity;

import java.util.List;
import java.util.Objects;
import javax.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(
    exclude = {"userGoals"})
@Table(name = "goals")
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String text;

    @OneToMany(mappedBy = "goal", fetch = FetchType.LAZY)
    private List<UserGoal> userGoals;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Goal goal = (Goal) o;
        return Objects.equals(id, goal.id)
            && Objects.equals(text, goal.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text);
    }
}
