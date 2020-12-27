package greencity.entity;

import greencity.dto.user.RegistrationStatisticsDtoResponse;
import greencity.enums.EmailNotification;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@SqlResultSetMapping(
    name = "monthsStatisticsMapping",
    classes = {
        @ConstructorResult(
            targetClass = RegistrationStatisticsDtoResponse.class,
            columns = {
                @ColumnResult(name = "month", type = Integer.class),
                @ColumnResult(name = "count", type = Long.class)
            })
    })
@NamedNativeQuery(name = "User.findAllRegistrationMonths",
    query = "SELECT EXTRACT(MONTH FROM date_of_registration) - 1 as month, count(date_of_registration) FROM users "
        + "WHERE EXTRACT(YEAR from date_of_registration) = EXTRACT(YEAR FROM CURRENT_DATE) "
        + "GROUP BY month",
    resultSetMapping = "monthsStatisticsMapping")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "users")
@EqualsAndHashCode(
    exclude = {"lastVisit", "verifyEmail", "ownSecurity",
        "refreshTokenKey", "restorePasswordEmail"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(unique = true, nullable = false, length = 50)
    private String email;

    @Enumerated(value = EnumType.ORDINAL)
    @Column(nullable = false)
    private Role role;

    @Enumerated(value = EnumType.ORDINAL)
    private UserStatus userStatus;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "HH:mm")
    private LocalDateTime lastVisit;

    @Column(nullable = false)
    private LocalDateTime dateOfRegistration;

    @OneToOne(mappedBy = "user", cascade = CascadeType.PERSIST)
    private OwnSecurity ownSecurity;

    @OneToOne(mappedBy = "user", cascade = CascadeType.PERSIST)
    private VerifyEmail verifyEmail;

    @OneToOne(mappedBy = "user")
    private RestorePasswordEmail restorePasswordEmail;

    @Enumerated(value = EnumType.ORDINAL)
    private EmailNotification emailNotification;

    @Column(name = "refresh_token_key", nullable = false)
    private String refreshTokenKey;

    @Column(name = "profile_picture")
    private String profilePicturePath;

    @OneToMany
    @JoinTable(name = "users_friends",
        joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "friend_id", referencedColumnName = "id"))
    private List<User> userFriends = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UserAchievement> userAchievements = new ArrayList<>();

    @Column(name = "rating")
    private Double rating;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "city")
    private String city;

    @Column(name = "user_credo")
    private String userCredo;

    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE)
    @Column(name = "social_networks")
    private List<SocialNetwork> socialNetworks;

    @Column(name = "show_location")
    private Boolean showLocation;

    @Column(name = "show_eco_place")
    private Boolean showEcoPlace;

    @Column(name = "show_shopping_list")
    private Boolean showShoppingList;

    @Column(name = "last_activity_time")
    private LocalDateTime lastActivityTime;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserAction> userActions = new ArrayList<>();
}
