package greencity.entity;

import java.util.List;
import javax.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "goals")
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String text;

    @ManyToMany(mappedBy = "goals")
    private List<UserGoal> userGoals;
}
