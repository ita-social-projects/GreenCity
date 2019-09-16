package greencity.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.time.DayOfWeek;
import java.time.LocalTime;
import javax.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Getter
@Setter
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

    @Enumerated
    private DayOfWeek weekDay;

    @OneToOne(cascade = {CascadeType.ALL})
    private BreakTime breakTime;

    @ManyToOne
    @JsonBackReference
    private Place place;
}
