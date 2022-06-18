package greencity.entity.event;

import lombok.*;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "events_dates_locations")
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

    @Column(nullable = false)
    @NonNull
    private ZonedDateTime startDate;

    @Column(nullable = false)
    @NonNull
    private ZonedDateTime finishDate;

    @Embedded
    @Nullable
    private Coordinates coordinates;

    @Column
    @Nullable
    private String onlineLink;
}
