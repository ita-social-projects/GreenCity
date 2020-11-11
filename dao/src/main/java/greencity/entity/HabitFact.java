package greencity.entity;

import lombok.*;
import javax.persistence.*;
import java.util.List;

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

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "habitFact", fetch = FetchType.EAGER)
    private List<HabitFactTranslation> translations;

    @ManyToOne(fetch = FetchType.EAGER)
    private Habit habit;
}
