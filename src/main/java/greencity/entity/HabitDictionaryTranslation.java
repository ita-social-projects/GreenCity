package greencity.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "habit_dictionary_translation")
@EqualsAndHashCode(
        exclude = {"habitDictionary"})
@ToString(
        exclude = {"habitDictionary", "language"})
public class HabitDictionaryTranslation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String habitItem;

    @ManyToOne
    private Language language;

    @ManyToOne
    private HabitDictionary habitDictionary;
}
