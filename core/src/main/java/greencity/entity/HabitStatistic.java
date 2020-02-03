package greencity.entity;

import greencity.entity.enums.HabitRate;
import java.time.ZonedDateTime;
import javax.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "habit_statistics")
@EqualsAndHashCode(exclude = "habit")
@ToString(exclude = "habit")
@Entity
public class HabitStatistic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "rate")
    private HabitRate habitRate;

    @Column(name = "date", nullable = false)
    private ZonedDateTime createdOn;

    @Column(name = "amount_of_items")
    private Integer amountOfItems;

    @ManyToOne
    private Habit habit;
}
