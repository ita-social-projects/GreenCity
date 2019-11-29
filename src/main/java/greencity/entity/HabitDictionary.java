package greencity.entity;

import java.util.List;
import javax.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "habit")
@EqualsAndHashCode(exclude = "habit")
@Table(name = "habit_dictionary")
@Builder
public class HabitDictionary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "habitDictionary")
    private List<Habit> habit;
}
