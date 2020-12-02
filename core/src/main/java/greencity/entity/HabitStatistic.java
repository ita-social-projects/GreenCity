package greencity.entity;

import greencity.entity.enums.HabitRate;
import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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
