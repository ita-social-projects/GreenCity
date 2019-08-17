package greencity.entities;

import javax.persistence.*;
import java.time.LocalTime;
import greencity.entities.enums.WeekDay;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpeningHours {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime openTime;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime closeTime;

    @Enumerated private WeekDay weekDay;

    @ManyToOne private Place place;
}
