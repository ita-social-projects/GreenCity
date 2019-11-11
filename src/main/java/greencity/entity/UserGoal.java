package greencity.entity;

import greencity.entity.enums.GoalStatus;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "user_goals")
public class UserGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToMany
    @JoinTable(
        name = "user_user_goals",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "goal_id"))
    private List<Goal> goals;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private GoalStatus status = GoalStatus.ACTIVE;

    private LocalDateTime dateCompleted;
}
