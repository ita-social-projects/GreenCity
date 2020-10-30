package greencity.entity;

import java.util.List;
import javax.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
@Table(name = "habit_facts")
public class HabitFact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = {CascadeType.REMOVE, CascadeType.REFRESH}, mappedBy = "habitFact", fetch = FetchType.LAZY)
    private List<HabitFactTranslation> translations;

    @ManyToOne
    private Habit habit;
}

