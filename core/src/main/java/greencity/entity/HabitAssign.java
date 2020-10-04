package greencity.entity;

import java.time.ZonedDateTime;
import java.util.List;
import javax.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "habit_assign")
public class HabitAssign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(name = "acquired", nullable = false)
    private Boolean acquired;

    @Column(name = "create_date", nullable = false)
    private ZonedDateTime createDate;

    @Column(name = "suspended", nullable = false)
    private Boolean suspended;

    @ManyToOne
    private Habit habit;

    @ManyToOne
    private User user;

    @OneToOne(mappedBy = "habitAssign", cascade = CascadeType.ALL)
    private HabitStatus habitStatus;

    @OneToMany(mappedBy = "habitAssign", cascade = CascadeType.ALL)
    private List<HabitStatistic> habitStatistic;
}
