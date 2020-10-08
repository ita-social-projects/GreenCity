package greencity.entity;

import java.util.List;
import javax.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = "habit")
@EqualsAndHashCode(exclude = "habit")
@Table(name = "habit_dictionary")
@Builder
public class HabitDictionary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String image;

    @OneToMany(mappedBy = "habitDictionary")
    private List<Habit> habit;

    @OneToMany(mappedBy = "habitDictionary")
    private List<HabitDictionaryTranslation> habitDictionaryTranslations;
}
