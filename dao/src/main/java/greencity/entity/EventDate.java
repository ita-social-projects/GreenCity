package greencity.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_dates")
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class EventDate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(name = "start_date", nullable = false)
    @NonNull
    private LocalDateTime startDate;

    @Column(name = "finish_date", nullable = false)
    @NonNull
    private LocalDateTime finishDate;
}
