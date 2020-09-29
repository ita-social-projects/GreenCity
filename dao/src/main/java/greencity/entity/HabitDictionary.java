package greencity.entity;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
