package greencity.entity;

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

    @OneToOne
    private UserGoal goal;
}
