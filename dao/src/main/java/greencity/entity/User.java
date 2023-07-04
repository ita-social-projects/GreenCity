package greencity.entity;

import greencity.dto.friends.UserFriendDto;
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
import lombok.ToString;

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
import javax.persistence.ManyToMany;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@SqlResultSetMappings(value = {
    @SqlResultSetMapping(
        name = "monthsStatisticsMapping",
        classes = {
            @ConstructorResult(
                targetClass = RegistrationStatisticsDtoResponse.class,
                columns = {
                    @ColumnResult(name = "month", type = Integer.class),
                    @ColumnResult(name = "count", type = Long.class)
                })
        }),
    @SqlResultSetMapping(
        name = "userFriendDtoMapping",
        classes = {
            @ConstructorResult(
                targetClass = UserFriendDto.class,
                columns = {
                    @ColumnResult(name = "id", type = Long.class),
                    @ColumnResult(name = "name", type = String.class),
                    @ColumnResult(name = "city", type = String.class),
                    @ColumnResult(name = "rating", type = Double.class),
                    @ColumnResult(name = "mutualFriends", type = Long.class),
                    @ColumnResult(name = "profilePicturePath", type = String.class),
                    @ColumnResult(name = "chatId", type = Long.class),
                })
        })
})
@NamedNativeQueries(value = {
    @NamedNativeQuery(name = "User.findAllRegistrationMonths",
        query = "SELECT EXTRACT(MONTH FROM date_of_registration) - 1 as month, count(date_of_registration) FROM users "
            + "WHERE EXTRACT(YEAR from date_of_registration) = EXTRACT(YEAR FROM CURRENT_DATE) "
            + "GROUP BY month",
        resultSetMapping = "monthsStatisticsMapping"),
    @NamedNativeQuery(name = "User.getAllUsersExceptMainUserAndFriends",
        query = "SELECT *, (SELECT count(*) "
            + "        FROM users_friends uf1 "
            + "        WHERE uf1.user_id in :friends "
            + "          and uf1.friend_id = u.id "
            + "          and uf1.status = 'FRIEND' "
            + "           or "
            + "         uf1.friend_id in :friends "
            + "          and uf1.user_id = u.id "
            + "          and uf1.status = 'FRIEND') as mutualFriends, "
            + "       u.profile_picture           as profilePicturePath, "
            + "       (SELECT p.room_id "
            + "       FROM chat_rooms_participants p"
            + "       WHERE p.participant_id IN (u.id, :userId) "
            + "       GROUP BY p.room_id "
            + "       HAVING COUNT(DISTINCT p.participant_id) = 2 LIMIT 1) as chatId "
            + "FROM users u "
            + "WHERE u.id != :userId "
            + "AND u.id NOT IN :friends AND LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%')) ",
        resultSetMapping = "userFriendDtoMapping")
})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "users")
@EqualsAndHashCode(
    exclude = {"verifyEmail", "ownSecurity", "ecoNewsLiked", "ecoNewsCommentsLiked",
        "refreshTokenKey", "verifyEmail", "estimates", "restorePasswordEmail", "customShoppingListItems",
        "eventOrganizerRating"})
@ToString(
    exclude = {"verifyEmail", "ownSecurity", "refreshTokenKey", "ecoNewsLiked", "ecoNewsCommentsLiked",
        "verifyEmail", "estimates", "restorePasswordEmail", "customShoppingListItems", "eventOrganizerRating"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(unique = true, nullable = false, length = 50)
    private String email;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(value = EnumType.ORDINAL)
    private UserStatus userStatus;

    @Column(nullable = false)
    private LocalDateTime dateOfRegistration;

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

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<CustomShoppingListItem> customShoppingListItems = new ArrayList<>();

    @Column(name = "profile_picture")
    private String profilePicturePath;

    @ManyToMany(mappedBy = "usersLikedNews")
    private Set<EcoNews> ecoNewsLiked;

    @ManyToMany(mappedBy = "usersLiked")
    private Set<EcoNewsComment> ecoNewsCommentsLiked;

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

    @Column(name = "event_organizer_rating")
    private Double eventOrganizerRating;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserAction> userActions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Filter> filters = new ArrayList<>();
}
