package greencity.entity;

import javax.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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
