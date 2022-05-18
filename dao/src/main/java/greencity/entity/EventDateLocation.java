package greencity.entity;

import lombok.*;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_dates_locations")
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class EventDateLocation {
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

    @Embedded
    @Nullable
    private Coordinates coordinates;

    @Column(name = "online_link")
    @Nullable
    private String onlineLink;
}
