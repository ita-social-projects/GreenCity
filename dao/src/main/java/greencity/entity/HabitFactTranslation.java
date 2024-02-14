package greencity.entity;

import greencity.enums.FactOfDayStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.IntegerJdbcType;

@Getter
@Setter
@Entity
@Table(name = "habit_fact_translations")
@SuperBuilder
@EqualsAndHashCode(callSuper = true, exclude = {"factOfDayStatus", "habitFact"})
@NoArgsConstructor
public class HabitFactTranslation extends Translation {
    @Enumerated(value = EnumType.ORDINAL)
    @JdbcType(IntegerJdbcType.class)
    private FactOfDayStatus factOfDayStatus;

    @ManyToOne
    private HabitFact habitFact;
}
