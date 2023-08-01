package greencity.entity.event;

import greencity.entity.Tag;
import greencity.entity.User;
import greencity.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "events")
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NonNull
    private String title;

    @Column
    private String titleImage;

    @ManyToOne
    private User organizer;

    @Column
    private LocalDate creationDate;

    @Column
    @NonNull
    private String description;

    @ManyToMany
    @JoinTable(name = "events_attenders",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> attenders = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "events_followers",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> followers;

    @NonNull
    @OrderBy("finishDate ASC")
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<EventDateLocation> dates = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<EventImages> additionalImages = new ArrayList<>();

    @Column
    private boolean isOpen = true;

    @ManyToMany
    @JoinTable(name = "events_tags",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<EventGrade> eventGrades = new ArrayList<>();

    /**
     * Method for getting an event type.
     *
     * @return {@link EventType} instance.
     * @author Olena Sotnik.
     */
    public EventType getEventType() {
        if (dates.stream().allMatch(date -> Objects.nonNull(date.getOnlineLink())
            && Objects.nonNull(date.getAddress()))) {
            return EventType.ONLINE_OFFLINE;
        }
        if (dates.stream().anyMatch(date -> Objects.nonNull(date.getOnlineLink()))) {
            return EventType.ONLINE;
        }
        return EventType.OFFLINE;
    }
}
