package greencity.entity;

import greencity.entity.enums.EmailNotification;
import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "users")
@EqualsAndHashCode(
    exclude = {"places", "comments", "verifyEmail", "addedPlaces", "favoritePlaces", "ownSecurity", "refreshTokenKey",
        "verifyEmail", "estimates", "restorePasswordEmail", "addedEcoNews"})
@ToString(
    exclude = {"places", "comments", "verifyEmail", "addedPlaces", "favoritePlaces", "ownSecurity", "refreshTokenKey",
        "verifyEmail", "estimates", "restorePasswordEmail", "addedEcoNews"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(unique = true, nullable = false, length = 50)
    private String email;

    @Enumerated(value = EnumType.ORDINAL)
    @Column(nullable = false)
    private ROLE role;

    @Enumerated(value = EnumType.ORDINAL)
    private UserStatus userStatus;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "HH:mm")
    private LocalDateTime lastVisit;

    @Column(nullable = false)
    private LocalDateTime dateOfRegistration;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<Place> places = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<FavoritePlace> favoritePlaces = new ArrayList<>();

    @OneToMany(mappedBy = "author")
    private List<Place> addedPlaces = new ArrayList<>();

    @OneToMany(mappedBy = "author")
    private List<EcoNews> addedEcoNews = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.PERSIST)
    private OwnSecurity ownSecurity;

    @OneToOne(mappedBy = "user", cascade = CascadeType.PERSIST)
    private VerifyEmail verifyEmail;

    @OneToOne(mappedBy = "user")
    private RestorePasswordEmail restorePasswordEmail;

    @OneToMany(mappedBy = "user")
    private List<Estimate> estimates = new ArrayList<>();
    @Enumerated(value = EnumType.ORDINAL)
    private EmailNotification emailNotification;

    @Column(name = "refresh_token_key", nullable = false)
    private String refreshTokenKey;

    @OneToMany(mappedBy = "user")
    private List<Habit> habits = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<UserGoal> userGoals = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<CustomGoal> customGoals = new ArrayList<>();

    @Column(name = "profile_picture")
    private String profilePicturePath;

    @OneToMany
    @JoinTable(name = "users_friends",
            joinColumns = @JoinColumn(name = "user_id",referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "friends",referencedColumnName = "id"))
    private List<User> userFriends = new ArrayList<>();
}
