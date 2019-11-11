package greencity.entity;

import greencity.entity.enums.GoalStatus;
import java.time.LocalDateTime;
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

    @OneToOne(mappedBy = "goal")
    private Goal goal;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private GoalStatus status = GoalStatus.ACTIVE;

    private LocalDateTime dateCompleted;
}
