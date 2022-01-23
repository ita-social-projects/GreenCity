package greencity.entity;

import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "events")
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NonNull
    private String title;

    @ManyToOne
    @NonNull
    private User organizer;

    @Column
    @NonNull
    private String description;

    @Column(nullable = false)
    @NonNull
    private ZonedDateTime dateTime;

    @Column
    @NonNull
    private String location;

    @ManyToMany
    @JoinTable(
            name = "events_attenders",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> attenders = new HashSet<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<EventImages> images;
}
