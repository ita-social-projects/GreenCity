package greencity.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"userGoals", "user"})
@Table(name = "custom_goals")
public class CustomGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "customGoal", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<UserGoal> userGoals = new ArrayList<>();
}
