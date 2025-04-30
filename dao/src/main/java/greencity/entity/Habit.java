package greencity.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "habits",
    indexes = @Index(name = "idx_habits_is_deleted", columnList = "is_deleted")
)
@EqualsAndHashCode(
    exclude = {"habitAssigns", "followers", "habitTranslations", "tags", "toDoListItems"})
@ToString(
    exclude = {"habitAssigns", "followers", "habitTranslations", "tags", "toDoListItems"})
public class Habit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(name = "image", nullable = false)
    private String image;

    @Column(name = "complexity", nullable = false)
    private Integer complexity;

    @Column(name = "default_duration", nullable = false)
    private Integer defaultDuration;

    @Column(name = "is_custom_habit", nullable = false)
    private Boolean isCustomHabit;

    @Column(name = "user_id")
    private Long userId;

    @BatchSize(size = 50)
    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CustomToDoListItem> customToDoListItems;

    @BatchSize(size = 50)
    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HabitTranslation> habitTranslations;

    @BatchSize(size = 50)
    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL)
    private List<HabitAssign> habitAssigns;

    @ManyToMany
    @JoinTable(
        name = "habit_to_do_list_items",
        joinColumns = @JoinColumn(name = "habit_id"),
        inverseJoinColumns = @JoinColumn(name = "to_do_list_item_id"))
    @BatchSize(size = 50)
    private Set<ToDoListItem> toDoListItems;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "habits_tags",
        joinColumns = @JoinColumn(name = "habit_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @BatchSize(size = 50)
    private Set<Tag> tags;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Transient
    @Builder.Default
    private boolean currentUserLiked = false;

    @ManyToMany
    @JoinTable(
        name = "habits_users_likes",
        joinColumns = @JoinColumn(name = "habit_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id"))
    @BatchSize(size = 50)
    private Set<User> usersLiked = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "habits_users_dislikes",
        joinColumns = @JoinColumn(name = "habit_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id"))
    @BatchSize(size = 50)
    private Set<User> usersDisliked = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @Builder.Default
    @JoinTable(name = "habits_followers",
        joinColumns = @JoinColumn(name = "habit_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id"))
    @BatchSize(size = 50)
    private Set<User> followers = new HashSet<>();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
