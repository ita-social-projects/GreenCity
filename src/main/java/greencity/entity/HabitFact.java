package greencity.entity;

import greencity.dto.advice.AdviceAdminDTO;
import greencity.dto.fact.HabitFactDTO;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "fact")
public class HabitFact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 300)
    private String fact;

    @ManyToOne
    private HabitDictionary habitDictionary;

    /**
     * The constructor takes {@link HabitFact} parameter.
     *
     * @param habitFactDTO {@link AdviceAdminDTO}
     * @author Vitaliy Dzen
     */
    public HabitFact(HabitFactDTO habitFactDTO, HabitDictionary habitDictionary) {
        this.fact = habitFactDTO.getFact();
        this.habitDictionary = habitDictionary;
    }
}
