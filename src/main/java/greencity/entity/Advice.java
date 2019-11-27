package greencity.entity;

import greencity.dto.advice.AdviceAdminDTO;
import greencity.dto.advice.AdvicePostDTO;
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
@Table(name = "advices")
public class Advice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 300)
    private String name;

    @ManyToOne
    private HabitDictionary habitDictionary;

    /**
     * The constructor takes {@link Advice} parameter.
     *
     * @param advicePostDTO {@link AdviceAdminDTO}
     * @author Vitaliy Dzen
     */
    public Advice(AdvicePostDTO advicePostDTO, HabitDictionary habitDictionary) {
        this.name = advicePostDTO.getAdvice();
        this.habitDictionary = new HabitDictionary(habitDictionary.getId(), habitDictionary.getName(), null);
    }
}
