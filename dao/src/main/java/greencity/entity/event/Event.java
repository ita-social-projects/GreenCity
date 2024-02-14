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
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "events")
@EqualsAndHashCode(exclude = {"attenders", "followers", "dates"})
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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "events_attenders",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> attenders = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "events_followers",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> followers = new HashSet<>();

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

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<EventComment> eventsComments = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "events_users_likes",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "users_id"))
    private Set<User> usersLikedEvents = new HashSet<>();

    /**
     * Method for getting an event type.
     *
     * @return {@link EventType} instance.
     * @author Olena Sotnik.
     */
    public EventType getEventType() {
        if (dates.stream().anyMatch(date -> Objects.nonNull(date.getOnlineLink())
            && Objects.nonNull(date.getAddress()))) {
            return EventType.ONLINE_OFFLINE;
        }
        if (dates.stream().allMatch(date -> Objects.nonNull(date.getOnlineLink()))) {
            return EventType.ONLINE;
        }
        return EventType.OFFLINE;
    }

    /**
     * Method for checking if event is Relevant.
     *
     * @return boolean: true if relevant, false if event passed.
     * @author Olena Sotnik.
     */
    public boolean isRelevant() {
        return dates.getLast().getFinishDate().isAfter(ZonedDateTime.now())
            || dates.getLast().getFinishDate().isEqual(ZonedDateTime.now());
    }
}
