package greencity.entities;

import greencity.entities.enums.WeekDays;
import java.time.LocalTime;

import javax.persistence.*;
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

    @Enumerated private WeekDays weekDays;
    @ManyToOne private Place place;
}
